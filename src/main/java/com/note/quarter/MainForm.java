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
        System.out.println(1);
        fxmlLoader = new FXMLLoader();
        System.out.println(2);
        fxmlLoader.setLocation(getClass().getResource("MainForm.fxml"));
        System.out.println(3);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        System.out.println(4);
        Parent root = fxmlLoader.load();
        System.out.println(5);

        System.out.println(6);
        primaryStage.setTitle("QuarterNote");
        System.out.println(7);
        Scene scene = new Scene(root, 1100, 600);
        System.out.println(8);

        System.out.println(9);
        primaryStage.setScene(scene);
        System.out.println(10);
        primaryStage.show();
        System.out.println(11);
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
