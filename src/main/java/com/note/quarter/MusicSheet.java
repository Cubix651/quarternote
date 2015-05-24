package com.note.quarter;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import javax.xml.bind.JAXBException;

public class MusicSheet {
    private MusicStaff musicStaff;
    private boolean isRecording;

    public void save() throws JAXBException {
        musicStaff.saveXML();
    }

    public MusicSheet(Canvas canvas, Pane canvasPane) {
        musicStaff = new MusicStaff(canvas, canvasPane);

    }

    public MusicStaff getMusicStaff() {
        return musicStaff;
    }

    public void addNoteRest(NoteRest noteRest) {
        musicStaff.drawNoteRest(noteRest);
    }

    public void addNoteRest(NoteRest noteRest, double positionY) {
        if(noteRest.isNote()) {
            Note note = (Note) noteRest;
            NotePitch pitch = musicStaff.computePitchFromYPosition(positionY);
            if(pitch == null)
                return;
            note.setPitch(pitch);
        }
        addNoteRest(noteRest);
    }


    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean isRecording) {
        this.isRecording = isRecording;
    }

    public void deleteLastNoteRest() {
        musicStaff.eraseLastNoteRest();
    }

}
