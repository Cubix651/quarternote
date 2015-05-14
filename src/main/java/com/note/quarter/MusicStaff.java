package com.note.quarter;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static java.lang.Math.round;

public class MusicStaff {

    public final double STAFF_HEIGHT;
    public final double GAP_BETWEEN_STAFFS;
    public final double GAP_BETWEEN_PITCHES;
    public final double NOTEREST_HEIGHT = 50;
    public final double NUMBER_OF_HIGHEST_NOTE_IN_SCALE = 13;
    public final NotePitch LOWEST_PITCH = new NotePitch(60);
    public final double NOTEREST_SHIFT = 6;

    public final double CLEF_X_POSITION = 0;
    public final double METER_Y_POSITION = 8;

    private double lowestPositionY; //C4??
    public double currentStaffPosition;

    private final double barGap = 20;

    public double wholeXGap = 180 + 2;   //to do : change with constructor - deafult =180 - gap for whole note
    public final double MIN_X_POSITION = 60;
    public final double MAX_X_POSITION = 850; //width
    public double currentXPosition = MIN_X_POSITION;
    public double currentRelativeXPosition = 0;
    public final int LINES_COUNT;

    private Canvas canvas;
    private Pane canvasPane;

    public MusicStaff(Canvas canvas, Pane canvasPane)
    {
        STAFF_HEIGHT = ImageResource.getStaff().getHeight();
        GAP_BETWEEN_STAFFS = STAFF_HEIGHT;
        GAP_BETWEEN_PITCHES = STAFF_HEIGHT/8;

        lowestPositionY = STAFF_HEIGHT + 2 * GAP_BETWEEN_PITCHES;

        currentStaffPosition = GAP_BETWEEN_STAFFS;
        LINES_COUNT = drawStaff(canvas);

        this.canvas = canvas;
        this.canvasPane = canvasPane;
    }

    public int drawStaff(Canvas canvas)
    {
        double sheetHeight = canvas.getHeight();
        int counter = 0;
        double y = GAP_BETWEEN_STAFFS;
        while(y+STAFF_HEIGHT+ GAP_BETWEEN_STAFFS <sheetHeight)
        {
            canvas.getGraphicsContext2D().drawImage(ImageResource.getStaff(),0,y);
            Image clef = ImageResource.getClef();
            canvas.getGraphicsContext2D().drawImage(clef, CLEF_X_POSITION, y - (clef.getHeight() - STAFF_HEIGHT) / 2);
            Image meter = ImageResource.getMeter();
            canvas.getGraphicsContext2D().drawImage(meter, METER_Y_POSITION,y - (meter.getHeight() - STAFF_HEIGHT) / 2);
            y += GAP_BETWEEN_STAFFS + STAFF_HEIGHT;
            counter++;
        }
        return counter;
    }

    public NotePitch computePitchFromYPosition(double y) {
        double relativeY = currentStaffPosition + lowestPositionY - y;
        relativeY -= NOTEREST_HEIGHT / 2;
        relativeY += NOTEREST_SHIFT;
        int n = (int) round(relativeY / GAP_BETWEEN_PITCHES);
        if (n < 0 || n > NUMBER_OF_HIGHEST_NOTE_IN_SCALE) return null;
        return new NotePitch(LOWEST_PITCH.getIndex() + n, false);
    }

    public double computeYPositionFromPitch(NotePitch pitch) {
        double y = currentStaffPosition + lowestPositionY;
        y -= (pitch.getIndex() - LOWEST_PITCH.getIndex()) * GAP_BETWEEN_PITCHES;
        y -= NOTEREST_HEIGHT;
        y += NOTEREST_SHIFT;
        return y;
    }

    private boolean checkAndUpdateMeter(double defaultXGap)
    {
        if(currentRelativeXPosition+defaultXGap>wholeXGap || currentXPosition+defaultXGap> MAX_X_POSITION)
        {
            if(currentRelativeXPosition!=wholeXGap) return false;  //it illegal to insert a note;
            ImageView barImage = new ImageView(ImageResource.getBar());
            barImage.setLayoutX(currentXPosition);
            barImage.setLayoutY(currentStaffPosition);
            canvasPane.getChildren().add(barImage);
            currentXPosition = currentXPosition + barGap;

            if(currentRelativeXPosition+barGap>wholeXGap)
                currentRelativeXPosition = 0;
            if(currentXPosition+barGap> MAX_X_POSITION)
            {
                currentXPosition = MIN_X_POSITION;
                currentStaffPosition += STAFF_HEIGHT+GAP_BETWEEN_STAFFS;
                if(currentStaffPosition > canvas.getHeight()) return false; //no more place to insert
            }

        }
        return true;
    }

    public void drawNoteRest(NoteRest noteRest) {
        ImageView imageView;
        if (noteRest.isNote())
            imageView = new ImageView(ImageResource.getUpNoteImage(noteRest.getValue()));
        else
            imageView = new ImageView(ImageResource.getRestImage(noteRest.getValue()));

        double defaultXGap = wholeXGap * noteRest.getValue().getRelativeValue();
        if (!checkAndUpdateMeter(defaultXGap)) return;

        double y;
        if (noteRest.isNote()) {
            Note note = (Note) noteRest;
            y = computeYPositionFromPitch(note.getPitch());
        } else {
            y = currentStaffPosition;
        }

        imageView.setLayoutY(y);
        imageView.setLayoutX(currentXPosition);
        currentXPosition += defaultXGap;
        currentRelativeXPosition += defaultXGap;
        canvasPane.getChildren().add(imageView);
    }
}
