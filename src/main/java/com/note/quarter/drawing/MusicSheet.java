package com.note.quarter.drawing;

import com.note.quarter.noterest.Note;
import com.note.quarter.noterest.NotePitch;
import com.note.quarter.noterest.NoteRest;
import javafx.scene.layout.Pane;

public class MusicSheet {
    private MusicStaff musicStaff;
    private boolean isRecording;

    public MusicSheet(Pane sheetPane) {
        musicStaff = new MusicStaff(sheetPane);

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
