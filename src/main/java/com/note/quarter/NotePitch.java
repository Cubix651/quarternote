package com.note.quarter;

public class NotePitch {
    private int midiCode;
    public static final int SEMITONES_IN_SCALE = 12;
    public static final int TONES_IN_SCALE = 7;

    public NotePitch(int midiCode) {
        this.midiCode = midiCode;
    }

    public NotePitch(int index, boolean withSharp) {
        int octave = index / TONES_IN_SCALE;
        index %= TONES_IN_SCALE;
        if(index <= 2) {
            midiCode = 2*index;
        }
        else {
            midiCode = 2*index - 1;
        }
        if(withSharp)
            midiCode += 1;
        midiCode += SEMITONES_IN_SCALE * octave;
    }

    public int getMidiCode() {
        return midiCode;
    }

    public void setMidiCode(int midiCode) {
        this.midiCode = midiCode;
    }

    public int getIndex() {
        int relCode = midiCode % SEMITONES_IN_SCALE;
        int octave = midiCode / SEMITONES_IN_SCALE;
        if(relCode <= 4) {
            return TONES_IN_SCALE * octave + relCode / 2;
        } else {
            return TONES_IN_SCALE * octave + (relCode + 1) / 2;
        }
    }

    public boolean withSharp() {
        int relCode = midiCode % SEMITONES_IN_SCALE;
        if(relCode <= 4) {
            return relCode % 2 == 1;
        } else {
            return relCode % 2 == 0;
        }
    }
}
