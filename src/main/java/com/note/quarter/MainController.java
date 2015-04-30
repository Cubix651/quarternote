package com.note.quarter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import javax.sound.midi.MidiUnavailableException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Pane pianoPane;
    private Map<Integer, Button> pianoKeys = new HashMap<>();
    private MelodyPlayer melodyPlayer;

    private Map<String, Integer> keyboardMapping = new HashMap<>();
    String keyboardMapingSequence = "q2w3er5t6y7uzsxdcvgbhnjm";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (int i = 0; i < keyboardMapingSequence.length(); ++i) {
            keyboardMapping.put(keyboardMapingSequence.charAt(i) + "", 60 + i);
        }

        try {
            melodyPlayer = new MelodyPlayer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        for(Node node : pianoPane.getChildren()) {
            if (node.getId() != null && node.getId().startsWith("key")) {
                pianoKeys.put(Integer.parseInt(node.getId().substring(3)), (Button) node);
            }
        }
    }

    private void pressPianoKey(int noteNumber) {
        if (!melodyPlayer.isNoteOn(noteNumber)) {
            melodyPlayer.noteOn(noteNumber);
            Button button = pianoKeys.get(noteNumber);
            button.getStyleClass().replaceAll(s -> s + "Pressed");
        }
    }

    private void releasePianoKey(int noteNumber) {
        if (melodyPlayer.isNoteOn(noteNumber)) {
            melodyPlayer.noteOff(noteNumber);
            Button button = pianoKeys.get(noteNumber);
            button.getStyleClass().replaceAll(s -> s.substring(0, s.length() - "Pressed".length()));
        }
    }

    public void keyPressedHandler(KeyEvent event) {
        String key = event.getText();
        if(keyboardMapping.containsKey(key)) {
            pressPianoKey(keyboardMapping.get(key));
        }
    }

    public void keyReleasedHandler(KeyEvent event) {
        String key = event.getText();
        if(keyboardMapping.containsKey(key)) {
            releasePianoKey(keyboardMapping.get(key));
        }
    }

    public void pianoMousePressedHandler(MouseEvent event) {
        int noteNumber = Integer.parseInt(((Button) event.getSource()).getId().substring(3));
        pressPianoKey(noteNumber);
    }

    public void pianoMouseReleasedHandler(MouseEvent event) {
        int noteNumber = Integer.parseInt(((Button) event.getSource()).getId().substring(3));
        releasePianoKey(noteNumber);
    }
}
