package com.note.quarter;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.sound.midi.Sequencer;

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
                Sequencer sequencer = controller.getMetronomeScheduler().getSequencer();
                if(sequencer!=null && sequencer.isOpen()) sequencer.close();

            }


        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
