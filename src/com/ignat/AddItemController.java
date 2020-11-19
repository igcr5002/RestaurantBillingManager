package com.ignat;

import com.ignat.Datasource.Data;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;



//Controller for adding consumption to a table
public class AddItemController {


    public int selectedTableId;

    @FXML
    public GridPane gridPane;

    @FXML
    public void cancel(ActionEvent event) {
        closeStage(event);
    }


    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public GridPane getGridPane() {
        return gridPane;
    }
}
