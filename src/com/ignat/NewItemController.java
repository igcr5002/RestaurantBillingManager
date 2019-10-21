package com.ignat;

import com.ignat.Model.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;


//Controller for configuring the Menu
public class NewItemController {

    @FXML
    public TextField nameTextField;

    @FXML
    public TextField priceTextField;

    @FXML
    public DialogPane dialogPane;

    private String imageURL;


    public MenuItem processResults() {
        return new MenuItem(nameTextField.getText(),Float.valueOf(priceTextField.getText()),imageURL);
    }

    @FXML
    public void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("All Images", "*.*")
        );
        File file = fileChooser.showOpenDialog(dialogPane.getScene().getWindow());
        if(file!=null) {
            imageURL = file.toURI().toString();
        }
    }

    public void setMenuItem(MenuItem editedMenuItem) {
        nameTextField.setText(editedMenuItem.getItemName());
        priceTextField.setText(String.valueOf(editedMenuItem.getItemPrice()));
        imageURL = editedMenuItem.getImageURL();
    }

}
