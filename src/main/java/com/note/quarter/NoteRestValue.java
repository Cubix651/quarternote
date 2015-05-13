package com.note.quarter;

public enum NoteRestValue {
    WHOLE(1),
    HALF(0.5),
    QUARTER(0.25),
    EIGHTH(0.125);

    private double relativeValue;

    public double getRelativeValue() {
        return relativeValue;
    }

    NoteRestValue(double relativeValue) {
        this.relativeValue = relativeValue;
    }
}
