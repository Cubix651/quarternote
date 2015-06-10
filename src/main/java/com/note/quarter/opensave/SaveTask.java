package com.note.quarter.opensave;

import com.note.quarter.drawing.SheetItem;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.util.List;


public class SaveTask extends Task{

    private Label label;
    private List<SheetItem> notes;
    private File f;
    private StackPane pane;

    public SaveTask(Label label, List<SheetItem> notes, File f, StackPane pane){
        this.label = label;
        this.notes = notes;
        this.f = f;
        this.pane = pane;
    }

    @Override
    protected Object call() throws Exception {
        MusicXMLBuilder builder = new MusicXMLBuilder();
        setProgressIndicator(label);
        builder.save(notes,f);
        updateLabel(label);
        return null;
    }

    public void updateLabel(Label im)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.getChildren().remove(label);
            }
        });
    }
    private void setProgressIndicator(final Label v)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                v.setGraphic(new ProgressIndicator());
            }
        });

    }
}
