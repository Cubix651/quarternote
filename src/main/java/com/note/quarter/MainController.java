package com.note.quarter;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
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
    @FXML private ToggleButton metronomeButton;
    private final DataFormat NOTE_FORMAT = new DataFormat("note");

    private MusicSheet musicSheet;
    private MetronomeScheduler metronomeScheduler;
    private Map<Integer, TimeButton> pianoKeys = new HashMap<>();
    private MelodyPlayer melodyPlayer;
    private Map<String, Integer> keyboardMapping = new HashMap<>();

    String keyboardMappingSequence = "q2w3er5t6y7uzsxdcvgbhnjm";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (int i = 0; i < keyboardMappingSequence.length(); ++i) {
            keyboardMapping.put(keyboardMappingSequence.charAt(i) + "", 60 + i);
        }

        metronomeScheduler = new MetronomeScheduler();
        musicSheet = new MusicSheet(canvas, canvasPane);

        try {
            melodyPlayer = new MelodyPlayer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        for (Node node : pianoPane.getChildren()) {
            if (node.getId() != null && node.getId().startsWith("key")) {
                pianoKeys.put(Integer.parseInt(node.getId().substring(3)), (TimeButton) node);
            }
        }
    }

    private void pressPianoKey(int noteNumber) {
        if (!melodyPlayer.isNoteOn(noteNumber)) {
            melodyPlayer.noteOn(noteNumber);
            TimeButton button = pianoKeys.get(noteNumber);
            button.getStyleClass().replaceAll(s -> s + "Pressed");
            button.setPressedTime(System.nanoTime());

        }
    }

    private void releasePianoKey(int noteNumber) {
        if (melodyPlayer.isNoteOn(noteNumber)) {
            melodyPlayer.noteOff(noteNumber);
            TimeButton button = pianoKeys.get(noteNumber);
            long duration = (System.nanoTime()-button.getPressedTime())/1000000; //in miliseconds
            button.getStyleClass().replaceAll(s -> s.substring(0, s.length() - "Pressed".length()));
            if(musicSheet.isRecording()) {
                NoteRestValue value = metronomeScheduler.getNoteRestValueFromDuration(duration);
                musicSheet.addNoteRest(new Note(value, new NotePitch(noteNumber)));
            }
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
        event.consume();
    }

    public void restClickedHandler(Event event) {
        Button source = (Button)event.getSource();
        String[] split = source.getId().split("_");
        String value = split[0];
        musicSheet.addNoteRest(new Rest(NoteRestValue.valueOf(value.toUpperCase())));
        event.consume();
    }

    public void setUpBPMHanlder(KeyEvent event) {
        BPMTextField source = (BPMTextField)event.getSource();
        if(event.getCode().equals(KeyCode.ENTER))
        {
            metronomeScheduler.setBPM(Integer.parseInt(source.getText()));
            if(metronomeButton.isSelected()) {
                metronomeButton.setSelected(false);
                metronomeButton.setText("Metronome Off");
            }
        }

        event.consume();
    }

    public void metronomeClickedHandler(Event event) {
        ToggleButton source = (ToggleButton)event.getSource();
        if(source.isSelected())
        {
            source.setText("Metronome On");
            metronomeScheduler.metronomeOn();
        }
        else
        {
            metronomeScheduler.metronomeOff();
            source.setText("Metronome Off");
        }

    }

    public MetronomeScheduler getMetronomeScheduler()
    {
        return metronomeScheduler;
    }

    public void recordClickedHandler(Event event) {
        ToggleButton source = (ToggleButton)event.getSource();
        musicSheet.setRecording(source.isSelected());

    }
}
