package com.note.quarter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainForm extends Application {

    private FXMLLoader fxmlLoader;
    @Override
    public void start(Stage primaryStage) throws Exception {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("MainForm.fxml"));
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("QuarterNote");
        Scene scene = new Scene(root, 1100, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        MainController controller = fxmlLoader.getController();
        controller.performClose();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
