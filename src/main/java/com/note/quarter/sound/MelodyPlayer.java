package com.note.quarter.sound;

import com.note.quarter.noterest.Note;
import com.note.quarter.noterest.NoteRest;
import com.note.quarter.noterest.NoteRestValue;
import com.note.quarter.noterest.Rest;

import javax.sound.midi.*;
import java.util.List;

public class MelodyPlayer {

    private static final int VELOCITY = 64;
    private final byte END_OF_FILE = 0x2F;

    private Sequencer sequencer;
    private Runnable onEndMelodyEvent = null;
    private float bpm = 60;

    public MelodyPlayer() throws MidiUnavailableException{
        sequencer = MidiSystem.getSequencer();
    }

    public void setOnEndMelodyEvent(Runnable onEndMelodyEvent) {
        this.onEndMelodyEvent = onEndMelodyEvent;
    }

    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

    public void play(List<NoteRest> notes) throws MidiUnavailableException, InvalidMidiDataException {
        if (!sequencer.isOpen())
            sequencer.open();
        Sequence sequence = new Sequence(Sequence.PPQ, NoteRestValue.QUARTER.getRelativeValue(NoteRestValue.EIGHTH));
        Track track = sequence.createTrack();
        fillInTrack(track, notes);
        sequencer.setSequence(sequence);
        sequencer.setTempoInBPM(bpm);
        sequencer.addMetaEventListener((msg) -> {
                if(msg.getType() == END_OF_FILE) {
                    if(onEndMelodyEvent != null)
                        onEndMelodyEvent.run();
                    stop();
                }
            });
        sequencer.start();
    }

    public void stop() {
        if(sequencer != null && sequencer.isOpen()) {
            if (sequencer.isRunning())
                sequencer.stop();
            sequencer.close();
        }
    }

    public void close() {
        stop();
    }

    private void fillInTrack(Track track, List<NoteRest> notes) throws InvalidMidiDataException{
        int tick = 0;
        for (NoteRest item : notes) {
            if (item instanceof Note) {
                Note note = (Note) item;
                track.add(createNoteOnEvent(note.getPitch().getMidiCode(), tick));
                tick += note.getValue().getRelativeValue(NoteRestValue.EIGHTH);
                track.add(createNoteOffEvent(note.getPitch().getMidiCode(), tick));
            } else if (item instanceof Rest) {
                tick += item.getValue().getRelativeValue(NoteRestValue.EIGHTH);
            }
        }
    }

    private static MidiEvent createNoteOnEvent(int key, long tick) throws InvalidMidiDataException {
        return createNoteEvent(ShortMessage.NOTE_ON, key, VELOCITY, tick);
    }

    private static MidiEvent createNoteOffEvent(int key, long tick) throws InvalidMidiDataException{
        return createNoteEvent(ShortMessage.NOTE_OFF, key, 0, tick);
    }

    private static MidiEvent createNoteEvent(int command, int key, int velocity, long tick) throws InvalidMidiDataException{
        ShortMessage message = new ShortMessage();
        message.setMessage(command, 0, key, velocity);
        return new MidiEvent(message, tick);
    }
}