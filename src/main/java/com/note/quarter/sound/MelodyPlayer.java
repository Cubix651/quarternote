package com.note.quarter.sound;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.HashSet;
import java.util.Set;

public class MelodyPlayer {

    private int volume = 80;
    private Synthesizer synthesizer;
    private MidiChannel mainChannel;
    final private int mainChannelNumber = 0;
    private Set<Integer> notesOn = new HashSet<Integer>();

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public MelodyPlayer() throws MidiUnavailableException{
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        mainChannel = synthesizer.getChannels()[mainChannelNumber];
    }

    public void noteOn(int noteNumber) {
        mainChannel.noteOn(noteNumber, volume);
        notesOn.add(noteNumber);
    }

    public void noteOff(int noteNumber) {
        mainChannel.noteOff(noteNumber);
        notesOn.remove(noteNumber);
    }

    public void allNotesOff() {
        mainChannel.allNotesOff();
    }

    public boolean isNoteOn(int noteNumber) {
        return notesOn.contains(noteNumber);
    }
}
