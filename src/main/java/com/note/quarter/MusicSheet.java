package com.note.quarter;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.List;


public class MusicSheet {
    private MusicStaff musicStaff;
    private List<NoteRest> noteRestList = new LinkedList<>();
    private boolean isRecording;

    public MusicSheet(Canvas canvas, Pane canvasPane) {
        musicStaff = new MusicStaff(canvas, canvasPane);

    }

    public void addNoteRest(NoteRest noteRest) {
        noteRestList.add(noteRest);
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
}
