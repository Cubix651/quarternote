package com.note.quarter;

public class Rest extends NoteRest {
    public Rest(NoteRestValue value) {
        setValue(value);
    }
    @Override
    public boolean isNote() {
        return false;
    }
}
