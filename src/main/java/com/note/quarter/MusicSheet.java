package com.note.quarter;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;


public class MusicSheet {
    private MusicStaff musicStaff;

    public MusicSheet(Canvas canvas, Pane canvasPane) {
        musicStaff = new MusicStaff(canvas, canvasPane);
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
}
