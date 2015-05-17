package com.note.quarter;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class MainForm extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("MainForm.fxml"));
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("QuarterNote");
        Scene scene = new Scene(root, 1100, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                MainController controller = fxmlLoader.getController();
                Metronome m = controller.getMetronomeScheduler().getMetronome();
                if(m!=null) m.turnOff();
                ExecutorService s = controller.getMetronomeScheduler().getExecutorService();
                if(s!=null) {
                    try {
                        s.shutdown();
                    } catch (SecurityException exc) {
                        exc.printStackTrace();
                    }
                    try {
                        s.awaitTermination(1, TimeUnit.DAYS);
                    } catch (InterruptedException exc) {
                       exc.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
