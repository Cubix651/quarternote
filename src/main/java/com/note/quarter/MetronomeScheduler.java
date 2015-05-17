package com.note.quarter;

import javafx.fxml.FXML;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.StrictMath.*;

/**
 * Created by stella on 17.05.15.
 */
public class MetronomeScheduler {

    @FXML
    private final int DEFAULT_BPM = 60;
    private final int DEFAULT_BPM_FREQUENCY = 1;
    private final long DEFAULT_BPM_TO_MILISECONDS = 1000;
    private final int SHORTEST_NOTE = 8;

    private int bpm = DEFAULT_BPM;
    private long bpmToMiliseconds = DEFAULT_BPM_TO_MILISECONDS;
    private long bpmToFrequency = DEFAULT_BPM_FREQUENCY;
    private long shortestNoteDuration = DEFAULT_BPM_TO_MILISECONDS/2;
    private Metronome metronome;

    public Metronome getMetronome() {
        return metronome;
    }

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private int getNumberOfShortestNotes(long miliseconds)
    {
        long n = round(miliseconds/shortestNoteDuration);
        if(n<1) n =1;
        if(n>SHORTEST_NOTE) n = SHORTEST_NOTE;
        n = (long)pow(2,round(log(n)));
        return (int)n;
    }

    private int log(long x)
    {
        int res=0;
        while((x>>=1)>0){res++;}
        return res;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public NoteRestValue getNoteRestValueFromDuration(long miliseconds)
    {
        int n = getNumberOfShortestNotes(miliseconds);
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

    public long BPMtoMiliSeconds(int BPM)
        {
        return round(DEFAULT_BPM_TO_MILISECONDS/(1.0*BPM*DEFAULT_BPM_FREQUENCY/DEFAULT_BPM));
    }

    public int getBPM() {
        return bpm;
    }

    public void setBPM(int BPM) {
        if(metronome!=null) metronome.turnOff();
        bpmToMiliseconds= BPMtoMiliSeconds(BPM);
        shortestNoteDuration = bpmToMiliseconds/2;
        bpmToFrequency = BPMtoFrequency(BPM);
        this.bpm = BPM;

    }

    public void metronomeOn()
    {
        metronome = new Metronome(bpmToMiliseconds);
        Thread t = new Thread(metronome);
        executorService.execute(t);
    }
    public void metronomeOff()
    {

        metronome.turnOff();
    }
}
