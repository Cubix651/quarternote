package com.note.quarter;

import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;

import javax.sound.midi.MidiUnavailableException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // @FXML
    //  Button C4;

    private MelodyPlayer melodyPlayer;

    private Map<String, Integer> map = new HashMap<String, Integer>();
    String seq = "q2w3er5t6y7uzsxdcvgbhnjm";


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        for (int i = 0; i < seq.length(); ++i) {
            map.put(seq.charAt(i) + "", 60 + i);
        }


        try {
            melodyPlayer = new MelodyPlayer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
   /*  C4.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                melodyPlayer.noteOn((map.get(event.getText())));
            }
        });*/
    }

    public void KeyWhiteOrBlackPressedHandle(KeyEvent event) {
        melodyPlayer.noteOn((map.get(event.getText())));
    }

    public void KeyWhiteOrBlackClickedHandle(Event event) {

        melodyPlayer.noteOn(map.get(((Button) event.getSource()).getText()));
    }
}
