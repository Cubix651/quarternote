package com.note.quarter;

public enum NoteValue {
    WHOLE(1),
    HALF(0.5),
    QUARTER(0.25),
    EIGHTH(0.125),
    SIZTEENTH(0.0625);

    private double relativeDuration;

    public double getRelativeDuration() {
        return relativeDuration;
    }

    NoteValue(double relativeDuration) {
        this.relativeDuration = relativeDuration;
    }
}
