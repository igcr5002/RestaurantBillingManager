package com.ignat;

import com.ignat.Datasource.Data;
import com.ignat.Model.Consumption;
import com.ignat.Model.MenuItem;
import com.ignat.Model.Table;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class Controller {

	
    @FXML
    private TableView<Consumption> tableView;

    @FXML
    private TableColumn consumptionColumn;

    @FXML
    private TableColumn quantityColumn;

    @FXML
    private TableColumn priceColumn;

    @FXML
    private ListView<Table> listView;

    @FXML
    private Label totalLabel;

    @FXML
    private BorderPane mainBorderPane;

    private int tableNumber;

    private final float TAXES = 0.19f;

    DecimalFormat df = new DecimalFormat("#.00");

    public void initialize() {
        consumptionColumn.prefWidthProperty().bind(tableView.widthProperty().subtract(quantityColumn.getWidth() + priceColumn.getWidth() + 2));
        listView.setItems(Data.getDataInstance().loadTables());
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Table>() {
            @Override
            public void changed(ObservableValue<? extends Table> observableValue, Table table, Table t1) {
                if (t1 != null) {
                    Table selectedTable = listView.getSelectionModel().getSelectedItem();
                    ObservableList<Consumption> cons = Data.getDataInstance().getTableConsumption(selectedTable.getTableNumber());
                    tableView.getItems().setAll(cons);
                    totalLabel.setText("TOTAL: " + Float.valueOf(df.format(Data.getDataInstance().getTotal(cons))));
                    tableNumber = listView.getSelectionModel().getSelectedItem().getTableNumber();

                }
            }
        });
        listView.getSelectionModel().selectFirst();
        Data.getDataInstance().loadMenu();

    }

    @FXML
    public void handleAddConsumption() {

        Dialog dialog = new Dialog();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add item to consumption list: ");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addItemWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Dialog cannot be loaded");
            e.printStackTrace();
            return;
        }

        AddItemController controller = fxmlLoader.getController();
        controller.selectedTableId = tableNumber;
        int i = 0;
        int j = 0;
        Image image;
        for (MenuItem item : Data.getDataInstance().getMenuItems()) {
            image = new Image(item.getImageURL(),50,50,false,false);
            Button newButton = new Button("", new ImageView(image));
            newButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Data.getDataInstance().addConsumptionToTable(item.getItemName(), item.getItemPrice(), tableNumber);
                    Node source = (Node) actionEvent.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();
                }
            });
            controller.getGridPane().add(newButton, i, j);
            if(++j>9) {
                i++;
                j=0;
            }
        }

        dialog.showAndWait();
        tableView.getItems().setAll(Data.getDataInstance().getTableConsumption(tableNumber));
        totalLabel.setText("TOTAL: " + Float.valueOf(df.format(Data.getDataInstance().getTotal(Data.getDataInstance().getTableConsumption(tableNumber)))));

    }

    @FXML
    public void handleRemoveConsumption() {
        Consumption selectedCons = tableView.getSelectionModel().getSelectedItem();
        Data.getDataInstance().removeConsumption(selectedCons);
        tableView.getItems().setAll(Data.getDataInstance().getTableConsumption(tableNumber));
        totalLabel.setText("TOTAL: " + Float.valueOf(df.format(Data.getDataInstance().getTotal(Data.getDataInstance().getTableConsumption(tableNumber)))));
    }

    @FXML
    public void handleTotalConsumption() {
        List<Consumption> consumptions = Data.getDataInstance().getTableConsumption(tableNumber);
        float total = Data.getDataInstance().getTotal(consumptions);
        float totalWithTaxes = total + total * TAXES;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CHECK OUT");
        alert.setHeaderText("Table : " + tableNumber);
        alert.setContentText("Price : " + Float.valueOf(df.format(total))
                + "\nTaxes : " + Float.valueOf(df.format(total * TAXES))
                + "\n\nAmount to pay : " + Float.valueOf(df.format(totalWithTaxes)));
        alert.showAndWait();
        for (Consumption consumption : consumptions) {
            Data.getDataInstance().deleteConsumption(consumption);
        }
        tableView.getItems().setAll();
        totalLabel.setText("TOTAL: 0");
    }

    @FXML
    public void addNewTable() {
        Data.getDataInstance().addTable();
    }

    @FXML
    public void removeTable() {
        Data.getDataInstance().removeTable();
    }

    @FXML
    public void addItem() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new item to menu");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newItemWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Cannot load dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        NewItemController itemController = fxmlLoader.getController();

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Data.getDataInstance().addItemToMenu(itemController.processResults());

        }
    }

    @FXML
    public void editItem() {
        //FIrst retrieve what menu item we want to change.
        MenuItem menuItem = new MenuItem();
        Dialog dialog = new Dialog();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit item from the menu");
        dialog.setHeaderText("Please select which item you want to edit from the Menu: ");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addItemWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Dialog cannot be loaded");
            e.printStackTrace();
            return;
        }

        AddItemController controller = fxmlLoader.getController();
        int i = 0;
        int j = 0;
        Image image;
        for (MenuItem item : Data.getDataInstance().getMenuItems()) {
            image = new Image(item.getImageURL(),50,50,false,false);
            Button newButton = new Button("", new ImageView(image));
            newButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    menuItem.setItemName(item.getItemName());
                    menuItem.setItemPrice(item.getItemPrice());
                    menuItem.setImageURL(item.getImageURL());
                    Node source = (Node) actionEvent.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();
                }
            });
            controller.getGridPane().add(newButton, i, j);
            if(++j>9) {
                i++;
                j=0;
            }
        }

        dialog.showAndWait();

        //Now we change the item
        dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit selected item from Menu: ");
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newItemWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Cannot load dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        NewItemController itemController = fxmlLoader.getController();
        itemController.setMenuItem(menuItem);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Data.getDataInstance().updateItemFromMenu(menuItem, itemController.processResults());

        }
    }

    @FXML
    public void deleteItem() {
        //FIrst retrieve what menu item we want to change.
        MenuItem menuItem = new MenuItem();
        Dialog dialog = new Dialog();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit item from the menu");
        dialog.setHeaderText("Please select which item you want to edit from the Menu: ");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addItemWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Dialog cannot be loaded");
            e.printStackTrace();
            return;
        }

        AddItemController controller = fxmlLoader.getController();
        int i = 0;
        int j = 0;
        Image image;
        for (MenuItem item : Data.getDataInstance().getMenuItems()) {
            image = new Image(item.getImageURL(),50,50,false,false);
            Button newButton = new Button("", new ImageView(image));
            newButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Data.getDataInstance().deleteItemFromMenu(item);
                    Node source = (Node) actionEvent.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();
                }
            });
            controller.getGridPane().add(newButton, i, j);
            if(++j>9) {
                i++;
                j=0;
            }
        }

        dialog.showAndWait();


    }

}
