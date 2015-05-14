package com.note.quarter;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Pane pianoPane;
    @FXML private Canvas canvas;
    @FXML private Pane canvasPane;
    private final DataFormat NOTE_FORMAT = new DataFormat("note");

    private MusicSheet musicSheet;
    private Map<Integer, Button> pianoKeys = new HashMap<>();
    private MelodyPlayer melodyPlayer;

    private Map<String, Integer> keyboardMapping = new HashMap<>();
    String keyboardMappingSequence = "q2w3er5t6y7uzsxdcvgbhnjm";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (int i = 0; i < keyboardMappingSequence.length(); ++i) {
            keyboardMapping.put(keyboardMappingSequence.charAt(i) + "", 60 + i);
        }

        musicSheet = new MusicSheet(canvas, canvasPane);

        try {
            melodyPlayer = new MelodyPlayer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        for (Node node : pianoPane.getChildren()) {
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
            musicSheet.addNoteRest(new Note(NoteRestValue.EIGHTH, new NotePitch(noteNumber)));
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
        if (keyboardMapping.containsKey(key)) {
            pressPianoKey(keyboardMapping.get(key));
        }
    }

    public void keyReleasedHandler(KeyEvent event) {
        String key = event.getText();
        if (keyboardMapping.containsKey(key)) {
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

    public void noteRestDragDetectedHandler(Event event) {
        Button source = (Button) event.getSource();
        String[] split = source.getId().split("_");
        String value = split[0];
        String kind = split[1];
        NoteRest noteRest = null;
        Image image = null;
        if(kind.equals("note")) {
            noteRest = new Note(NoteRestValue.valueOf(value.toUpperCase()), null);
            image = ImageResource.getUpNoteImage(noteRest.getValue());
        }
        else if(kind.equals("rest")) {
            noteRest = new Rest(NoteRestValue.valueOf(value.toUpperCase()));
            image = ImageResource.getRestImage(noteRest.getValue());
        }

        Dragboard dragboard = source.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        content.putImage(image);
        content.put(NOTE_FORMAT, noteRest);
        dragboard.setContent(content);
        event.consume();
    }

    public void noteRestDragOverHandler(DragEvent event) {
        if (event.getGestureSource() != event.getSource() && event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    public void noteRestDragDroppedHandler(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean isSuccessful = false;

        if(dragboard.hasContent(NOTE_FORMAT)) {
            NoteRest noteRest = (NoteRest) dragboard.getContent(NOTE_FORMAT);
                    double y = event.getY();
                    musicSheet.addNoteRest(noteRest, y);
                    isSuccessful = true;
        }
        event.setDropCompleted(isSuccessful);
        event.consume();
    }

    public void setUpNewProject(Event event) throws IOException {
        if (canvasPane.getChildren().size() > 1) {
            canvasPane.getChildren().remove(1, canvasPane.getChildren().size());
        }
        musicSheet = new MusicSheet(canvas, canvasPane);
    }
}
