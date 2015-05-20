package com.note.quarter;

public enum NoteRestValue {
    WHOLE(16),
    HALF(8),
    QUARTER(4),
    EIGHTH(2);

    private int relativeValue;

    public int getRelativeValue() {
        return relativeValue;
    }

    NoteRestValue(int relativeValue) {
        this.relativeValue = relativeValue;
    }
}
