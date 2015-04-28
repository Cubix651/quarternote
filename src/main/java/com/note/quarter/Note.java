package com.note.quarter;

public class Note {
    private int midiCode;
    private NoteValue duration;

    public int getMidiCode() {
        return midiCode;
    }

    public void setMidiCode(int midiCode) {
        this.midiCode = midiCode;
    }

    public NoteValue getDuration() {
        return duration;
    }

    public void setDuration(NoteValue duration) {
        this.duration = duration;
    }
}
