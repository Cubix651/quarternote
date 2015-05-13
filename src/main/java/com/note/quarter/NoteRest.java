package com.note.quarter;

public abstract class NoteRest {
    private NoteRestValue value;

    public NoteRestValue getValue() {
        return value;
    }

    public void setValue(NoteRestValue value) {
        this.value = value;
    }

    public abstract boolean isNote();
}
