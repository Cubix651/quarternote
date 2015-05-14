package com.note.quarter;

public class Note extends NoteRest {
    private NotePitch pitch;

    public Note(NoteRestValue value, NotePitch pitch) {
        this.pitch = pitch;
        setValue(value);
    }

    public NotePitch getPitch() {
        return pitch;
    }

    public void setPitch(NotePitch pitch) {
        this.pitch = pitch;
    }

    @Override
    public boolean isNote() {
        return true;
    }
}
