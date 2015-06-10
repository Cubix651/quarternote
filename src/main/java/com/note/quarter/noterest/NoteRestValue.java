package com.note.quarter.noterest;


public enum NoteRestValue {
    WHOLE(16),
    HALF(8),
    QUARTER(4),
    EIGHTH(2);

    private int absoluteValue;

    public int getRelativeValue() {
        return absoluteValue / EIGHTH.absoluteValue;
    }

    public int getRelativeValue(NoteRestValue to) {
        return absoluteValue / to.absoluteValue;
    }

    NoteRestValue(int absoluteValue) {
        this.absoluteValue = absoluteValue;
    }
}
