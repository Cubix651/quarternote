package com.note.quarter;

import javafx.concurrent.Task;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by stella on 16.05.15.
 */
public class Metronome extends Task {

    private long frequency;
    private AudioStream audioStream;
    boolean playSound = true;

    public Metronome(long frequency)
    {
        this.frequency = frequency;
    }

    public void turnOff()
    {
        playSound = false;
    }

    @Override
    protected Object call() throws Exception {
        while (playSound) {
            try {
                FileInputStream in = new FileInputStream(new File(Metronome.class.getResource("sounds/tick.wav").toURI()));
                AudioStream audioStream = new AudioStream(in);
                AudioPlayer.player.start(audioStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
        return null;
    }
}
