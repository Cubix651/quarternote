package com.note.quarter;

import com.note.quarter.controls.NumericTextField;
import com.note.quarter.controls.TimeButton;
import com.note.quarter.drawing.ImageResource;
import com.note.quarter.drawing.MusicSheet;
import com.note.quarter.noterest.*;
import com.note.quarter.opensave.Alerts;
import com.note.quarter.opensave.MusicXMLBuilder;
import com.note.quarter.opensave.OpenTask;
import com.note.quarter.opensave.SaveTask;
import com.note.quarter.sound.MelodyPlayer;
import com.note.quarter.sound.MetronomeScheduler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private TitledPane notesAndRests;
    @FXML
    private Pane pianoPane;
    @FXML
    private Pane sheetPane;
    @FXML
    private ToggleButton metronomeButton;
    @FXML
    private ToggleButton recordButton;
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

        try {
            metronomeScheduler = new MetronomeScheduler();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        musicSheet = new MusicSheet(sheetPane);

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

    private void setUpNewProject() {
        recordButton.setSelected(false);
        sheetPane.getChildren().clear();
        musicSheet = new MusicSheet(sheetPane);
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
            long duration = (System.nanoTime() - button.getPressedTime()) / 1000000; //in miliseconds
            button.getStyleClass().replaceAll(s -> s.substring(0, s.length() - "Pressed".length()));
            if (musicSheet.isRecording()) {
                NoteRestValue value = metronomeScheduler.getNoteRestValueFromDuration(duration);
                musicSheet.addNoteRest(new Note(value, new NotePitch(noteNumber)));
            }
        }
    }

    public void keyPressedHandler(KeyEvent event) {
        String key = event.getText().toLowerCase();
        if (keyboardMapping.containsKey(key)) {
            pressPianoKey(keyboardMapping.get(key));
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            musicSheet.deleteLastNoteRest();
        }
    }

    public void keyReleasedHandler(KeyEvent event) {
        String key = event.getText().toLowerCase();
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
        if (kind.equals("note")) {
            noteRest = new Note(NoteRestValue.valueOf(value.toUpperCase()), null);
            image = ImageResource.getUpNoteImage(noteRest.getValue());
        } else if (kind.equals("rest")) {
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

        if (dragboard.hasContent(NOTE_FORMAT)) {
            NoteRest noteRest = (NoteRest) dragboard.getContent(NOTE_FORMAT);
            double y = event.getY();
            musicSheet.addNoteRest(noteRest, y);
            isSuccessful = true;
        }
        event.setDropCompleted(isSuccessful);
        event.consume();
    }

    public void setUpNewProjectHandler(Event event) throws IOException {
        setUpNewProject();
        event.consume();
    }

    public void restClickedHandler(Event event) {
        Button source = (Button) event.getSource();
        String[] split = source.getId().split("_");
        String value = split[0];
        musicSheet.addNoteRest(new Rest(NoteRestValue.valueOf(value.toUpperCase())));
        event.consume();
    }

    public void setUpBPMHandler(KeyEvent event) {
        NumericTextField source = (NumericTextField) event.getSource();
        if (event.getCode().equals(KeyCode.ENTER)) {
            metronomeScheduler.setBPM(Integer.parseInt(source.getText()));
            if (metronomeButton.isSelected()) {
                metronomeButton.setSelected(false);
                metronomeButton.setText("Metronome Off");
            }
        }

        event.consume();
    }

    public void metronomeClickedHandler(Event event) {
        ToggleButton source = (ToggleButton) event.getSource();
        if (source.isSelected()) {
            source.setText("Metronome On");
            try {
                metronomeScheduler.metronomeOn();
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }
        } else {
            metronomeScheduler.metronomeOff();
            source.setText("Metronome Off");
        }

    }

    public MetronomeScheduler getMetronomeScheduler() {
        return metronomeScheduler;
    }

    public void recordClickedHandler(Event event) {
        ToggleButton source = (ToggleButton) event.getSource();
        musicSheet.setRecording(source.isSelected());
    }

    public void saveProjectHandler(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MusicXML", "*.xml");
        fileChooser.setInitialFileName("MyScore");
        fileChooser.getExtensionFilters().add(filter);
        Window window = sheetPane.getScene().getWindow();
        File f = fileChooser.showSaveDialog(window);

        if (f != null) {
            f = new File(f.getAbsolutePath() + '.' + filter.getExtensions().get(0).split("\\.")[1]);
            if (!f.exists()) {
                MusicXMLBuilder builder = new MusicXMLBuilder();
                builder.save(musicSheet.getMusicStaff().getNodes(), f);
            } else {

                Optional<ButtonType> result = Alerts.raiseConfirmationAlert("File already exists. Do you want to override it?", null).showAndWait();
                if (result.get().equals(ButtonType.OK)) {
                    MusicXMLBuilder builder = new MusicXMLBuilder();
                    builder.save(musicSheet.getMusicStaff().getNodes(), f);
                }
            }
        }
    }

    private void startOpenSaveTask(Task t)
    {
        pianoPane.setDisable(true);
        notesAndRests.setDisable(true);
        t.runningProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!t.isRunning())
                {
                    pianoPane.setDisable(false);
                    notesAndRests.setDisable(false);
                }
            }
        });
        Thread thread = new Thread(t);
        thread.start();
    }

    private void save(File f){
        Label label = new Label("Saving...");
        label.setLayoutX(100);
        label.setLayoutY(100);
        stackPane.getChildren().add(label);
        SaveTask t = new SaveTask(label,musicSheet.getMusicStaff().getNodes(),f,stackPane);
        startOpenSaveTask(t);
    }

    private void open(File f)
    {
        Label label = new Label("Opening...");
        label.setLayoutX(100);
        label.setLayoutY(100);
        stackPane.getChildren().add(label);
        OpenTask t = new OpenTask(label,musicSheet,f,stackPane);
        startOpenSaveTask(t);
    }

    public void openProjectHandler(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MusicXML", "*.xml");
        fileChooser.getExtensionFilters().add(filter);
        Window window = sheetPane.getScene().getWindow();
        File f = fileChooser.showOpenDialog(window);
        if (f != null) {


            if (!musicSheet.getMusicStaff().getNodes().isEmpty()) {
                Optional<ButtonType> result = Alerts.raiseConfirmOpeningAlert().showAndWait();
                if (result.get().getText().equals("New")) {
                    setUpNewProject();
                }
            }
            open(f);
        }

    }

    public void deleteLastHandler(Event event) {
        musicSheet.deleteLastNoteRest();
    }

    public void aboutHandler(Event event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("QuarterNote 1.0");
        alert.setContentText("Project for programming classes\n\nAuthors:\nAleksandra Nowak and Jakub Cisło" +
                "\n\nRelease date:\nJune of 2015\n\nhttps://bitbucket.org/quarternote/quarternote");

        alert.showAndWait();
    }
}
