package com.note.quarter;

import javax.sound.midi.*;

import static java.lang.StrictMath.pow;
import static java.lang.StrictMath.round;

public class MetronomeScheduler implements MetaEventListener {


    private final int DEFAULT_BPM = 60;
    private final int DEFAULT_BPM_FREQUENCY = 1;
    private final long DEFAULT_BPM_TO_MILISECONDS = 1000;
    private final int SHORTEST_NOTE = 8;
    private final int BUFFER_SIZE = 8;
    private final int PPQ = 1;
    private final byte END_OF_FILE = 0x2F;
    private final byte METRONOME_TICK_SOUND  = 35;

    private int bpm = DEFAULT_BPM;
    private long bpmToMilliseconds = DEFAULT_BPM_TO_MILISECONDS;
    private long shortestNoteDuration = DEFAULT_BPM_TO_MILISECONDS/2;
    private Sequencer sequencer;

    public Sequencer getSequencer() {
        return sequencer;
    }

    public MetronomeScheduler() throws MidiUnavailableException {
        sequencer = MidiSystem.getSequencer();
    }

    private int getNumberOfShortestNotes(long milliseconds) {
        long n = round(milliseconds/shortestNoteDuration);
        if(n<1) n =1;
        if(n>SHORTEST_NOTE) n = SHORTEST_NOTE;
        n = (long)pow(2,round(log(n)));
        return (int)n;
    }

    private int log(long x) {
        int res=0;
        while((x>>=1)>0){res++;}
        return res;
    }

    public NoteRestValue getNoteRestValueFromDuration(long milliseconds) {
        int n = getNumberOfShortestNotes(milliseconds);
        double l = n*NoteRestValue.EIGHTH.getRelativeValue();
        for(NoteRestValue v : NoteRestValue.values())
        {
            if(v.getRelativeValue() == l)
            {
                return v;
            }
        }
        return NoteRestValue.EIGHTH;
    }


    public long BPMtoFrequency(int BPM){
        return round(BPM*DEFAULT_BPM_FREQUENCY/DEFAULT_BPM);
    }

    public long BPMtoMiliSeconds(int BPM) {
        return round(DEFAULT_BPM_TO_MILISECONDS/(1.0*BPM*DEFAULT_BPM_FREQUENCY/DEFAULT_BPM));
    }

    public int getBPM() {
        return bpm;
    }

    public void setBPM(int BPM) {
        if(sequencer.isOpen()) sequencer.close();
        bpmToMilliseconds= BPMtoMiliSeconds(BPM);
        shortestNoteDuration = bpmToMilliseconds/2;
        this.bpm = BPM;
    }

    private int setSounds(Track track) throws InvalidMidiDataException {
        int tickTimeStamp = 1;
        for(int i=0;i<20;i++) {
            ShortMessage shortMessage = new ShortMessage();
            shortMessage.setMessage(ShortMessage.NOTE_ON+9, METRONOME_TICK_SOUND, 0x60);
            track.add(new MidiEvent(shortMessage, tickTimeStamp));
            tickTimeStamp++;
        }
        return tickTimeStamp;
    }

    private void setEndOfFile(Track track, int tickTimeStamp) throws InvalidMidiDataException {
        MetaMessage metaMessage = new MetaMessage();
        byte bytes[] = {};
        metaMessage.setMessage(END_OF_FILE,bytes, bytes.length);
        track.add(new MidiEvent(metaMessage,tickTimeStamp));
    }

    private void setChannel(Track track) throws InvalidMidiDataException {
        ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(0xC0,0x00, 0x09);
        track.add(new MidiEvent(shortMessage,0));
    }

    private void setMetronomeTrack(Track track, int BPM) throws InvalidMidiDataException {
       sequencer.setTempoInBPM(BPM);
        setChannel(track);
        int tickTimeStamp = setSounds(track);
        setEndOfFile(track,tickTimeStamp);
    }

    public Sequence createMetronomeSequence(int BPM) throws InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ,PPQ);
        Track metronomeTrack = sequence.createTrack();
        setMetronomeTrack(metronomeTrack,BPM);
        return sequence;
    }


    public void metronomeOn() throws MidiUnavailableException, InvalidMidiDataException {
        if(!sequencer.isOpen())
            sequencer.open();
        sequencer.addMetaEventListener(this);
        sequencer.setSequence(createMetronomeSequence(bpm));
        sequencer.start();
    }
    public void metronomeOff() {
        sequencer.close();
    }

    @Override
    public void meta(MetaMessage meta) {
        if(meta.getType() == END_OF_FILE) {
            System.out.println("here");
            sequencer.setTickPosition(1);
            sequencer.setTempoInBPM(bpm);
            sequencer.start();
        }
    }
}
