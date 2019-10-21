package com.ignat;

import com.ignat.Datasource.Data;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainwindow.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("My Restaurant Manager");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        if(!Data.getDataInstance().open()){
            Platform.exit();
        }
        Data.getDataInstance().loadData();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Data.getDataInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
