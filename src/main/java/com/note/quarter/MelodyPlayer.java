package com.note.quarter;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class MelodyPlayer {

    private int volume = 80;
    private Synthesizer synthesizer;
    private MidiChannel mainChannel;
    final private int mainChannelNumber = 0;

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
    }


    public void noteOff(int noteNumber) {
        mainChannel.noteOff(noteNumber);
    }

    public void allNotesOff() {
        mainChannel.allNotesOff();
    }
}
