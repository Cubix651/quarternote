package com.note.quarter;

import java.io.Serializable;


public abstract class NoteRest implements Serializable {

    private NoteRestValue value;

    public NoteRestValue getValue() {
        return value;
    }

    public void setValue(NoteRestValue value) {
        this.value = value;
    }

    public abstract boolean isNote();
}
