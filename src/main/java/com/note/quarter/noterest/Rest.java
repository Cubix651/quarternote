package com.note.quarter.noterest;

public class Rest extends NoteRest {
    public Rest(NoteRestValue value) {
        setValue(value);
    }
    @Override
    public boolean isNote() {
        return false;
    }
}
