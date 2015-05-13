package com.note.quarter;

public class Note extends NoteRest {
    private int midiCode;

    public Note(NoteRestValue value, int midiCode) {
        this.midiCode = midiCode;
        setValue(value);
    }

    public int getMidiCode() {
        return midiCode;
    }

    public void setMidiCode(int midiCode) {
        this.midiCode = midiCode;
    }

    @Override
    public boolean isNote() {
        return true;
    }
}
