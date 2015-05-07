package com.note.quarter;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;

import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Pane pianoPane;
    @FXML
    private Canvas canvas;
    @FXML
    private Pane canvasPane;

    private MusicSheet musicSheet;
    private Map<Integer, Button> pianoKeys = new HashMap<>();
    private MelodyPlayer melodyPlayer;

    private Map<String, Integer> keyboardMapping = new HashMap<>();
    String keyboardMapingSequence = "q2w3er5t6y7uzsxdcvgbhnjm";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (int i = 0; i < keyboardMapingSequence.length(); ++i) {
            keyboardMapping.put(keyboardMapingSequence.charAt(i) + "", 60 + i);
        }

        File file  = new File("src/main/resources/com/note/quarter/images/Music-staff-small.png");

        musicSheet = new MusicSheet(canvas,canvasPane,file.toURI().toString());

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

    public void setNoteDragDetected(Event event) {
        Button source = (Button)event.getSource();
        ImageView view = (javafx.scene.image.ImageView) source.getGraphic();

        Dragboard dragboard = source.startDragAndDrop(TransferMode.COPY);

        ClipboardContent content = new ClipboardContent();
        content.putImage(view.getImage());
        content.putString(source.getId());
        dragboard.setContent(content);
        event.consume();
    }

    public void setNoteDragOver(DragEvent event) {
        if(event.getGestureSource()!=event.getSource() && event.getDragboard().hasImage())
        {
           event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    public void setNoteDragDroped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean isSuccesful=false;

        if(dragboard.hasImage() && dragboard.hasString())
        {
            String [] info = dragboard.getString().split("_");
            if(info.length==3) {
                if(info[0].equals("NOTE")) {
                    NoteValue v = NoteValue.valueOf(info[1]); //50x50 default image size;
                    NoteCharacter c = NoteCharacter.valueOf(info[2]);
                    double y = event.getY();

                    musicSheet.setValidNote(dragboard.getImage(), y, v, c);

                    isSuccesful = true;
                }
                else if(info[0].equals("SIGN"))
                {
                    NoteCharacter c = NoteCharacter.valueOf(info[1]);
                    double y = event.getY();
                    double x = event.getX();
                    musicSheet.setValidSign(dragboard.getImage(),y,x,c);
                    isSuccesful = true;
                }
            }

        }
        event.setDropCompleted(isSuccesful);
        event.consume();

    }

    public void setUpNewProject(Event event) throws IOException {

        if(canvasPane.getChildren().size()>1)
            {
                canvasPane.getChildren().remove(1,canvasPane.getChildren().size());
            }

        File file  = new File("src/main/resources/com/note/quarter/images/Music-staff-small.png");
        musicSheet = new MusicSheet(canvas,canvasPane,file.toURI().toString());
    }

    public void NoteClicked(MouseEvent event)
    {

        Button source = (Button)event.getSource();
        double x = event.getScreenX();
        double y = event.getScreenY();
        String [] info = source.getId().split("_");
        if(info.length==3) {
            if(info[0].equals("NOTE")) {
                NoteValue v = NoteValue.valueOf(info[1]);
                NoteCharacter c = NoteCharacter.valueOf(info[2]);
                ImageView imageView = (ImageView) source.getChildrenUnmodifiable().get(0);
                musicSheet.setNote(imageView.getImage(), v, c, source, x, y);
            }

        }

        event.consume();
    }


}
