package com.note.quarter.noterest;


import java.util.ArrayList;
import java.util.Arrays;

public class NotePitch {
    private int midiCode;
    private String accidental;


    public static final ArrayList<String> indexToStep = new ArrayList<String>(Arrays.asList("C","D","E","F","G","A","B"));
    public static final ArrayList<Integer> blackKeysMidiCodes = new ArrayList<>(Arrays.asList(61,63,66,68,70,73,75,78,80,82,84));
    public static final int SEMITONES_IN_SCALE = 12;
    public static final int TONES_IN_SCALE = 7;

    public NotePitch(int midiCode) {
        this.midiCode = midiCode;
        if(blackKeysMidiCodes.contains(midiCode))
        {
            setAccidental("sharp");
        }
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
        if(withSharp) {
            midiCode += 1;
            accidental = "sharp";
        }
        midiCode += SEMITONES_IN_SCALE * octave;
    }

    public int getMidiCode() {
        return midiCode;
    }

    public void setMidiCode(int midiCode) {
        this.midiCode = midiCode;
    }


    public String getStep()
    {
        int i =getIndex();
        i = i % TONES_IN_SCALE;
        return indexToStep.get(i);
    }

    public static int getIndexFromStep(String step)
    {
        return indexToStep.indexOf(step);
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

    public int getOctave(){
        return midiCode/SEMITONES_IN_SCALE - 1;
    }


    public boolean withSharp() {
        int relCode = midiCode % SEMITONES_IN_SCALE;
        if(relCode <= 4) {
            return relCode % 2 == 1;
        } else {
            return relCode % 2 == 0;
        }
    }

    public String getAccidental() {
        return accidental;
    }

    public void setAccidental(String accidental) {
        if(accidental.equals("flat") || accidental.equals("sharp") || accidental.equals("natural"))
        this.accidental = accidental;
    }
}
