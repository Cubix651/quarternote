package com.note.quarter;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import static java.lang.Math.round;


/**
 * Created by stella on 12.05.15.
 */
public class MusicStaff {

    public final double STAFF_GAP;
    public final double STAFF_WIDTH;
    public final double STAFF_HEIGHT;
    public final double REAL_LINE_GAP;
    public final double LINE_GAP;
    public final double INPUT_LINE_BOUND;
    public final double CLEF_POSITION = 0;
    public final double METER_POSITION;


    private double lowestDownCentrePositionY; //C4??
    private double lowestUpCentrePositionY;
    private double center;
    private Image image;
    private Image meter;
    private Image clef;

    public MusicStaff(Image image, Image meter, Image clef)
    {
        this.image = image;
        this.meter = meter;
        this.clef = clef;

        STAFF_GAP = image.getHeight();
        STAFF_HEIGHT = image.getHeight();
        STAFF_WIDTH = image.getWidth();
        REAL_LINE_GAP = STAFF_HEIGHT/4;
        LINE_GAP = REAL_LINE_GAP/2;
        INPUT_LINE_BOUND = LINE_GAP/2;
        METER_POSITION = 0.4*STAFF_HEIGHT;

        this.lowestDownCentrePositionY  = STAFF_HEIGHT/2 + REAL_LINE_GAP + LINE_GAP;
        this.lowestUpCentrePositionY = lowestDownCentrePositionY+STAFF_HEIGHT/2 + REAL_LINE_GAP;
        center = STAFF_HEIGHT/2;
    }

    public int drawStaff(Canvas canvas, double sheetLength, double sheetWidth, double meterPosition) //mP 8
    {
        int counter = 0;
        double y = STAFF_GAP+STAFF_HEIGHT;
        while(y+STAFF_HEIGHT+STAFF_GAP<sheetLength)
        {
            canvas.getGraphicsContext2D().drawImage(image,0,y,sheetWidth,STAFF_HEIGHT);
            canvas.getGraphicsContext2D().drawImage(clef,CLEF_POSITION,y-center);
            canvas.getGraphicsContext2D().drawImage(meter,meterPosition,y-center);
            y = y+STAFF_GAP+STAFF_HEIGHT;
            counter++;
        }
        return counter;
    }

    public double getValidNotePosition(double positionY, boolean down, double currentStaffPosition) throws IllegalNotePosition
    {
            if(down) {
                positionY-=currentStaffPosition;
                long n = round((lowestDownCentrePositionY - positionY) / LINE_GAP);
                if (n < 0 || n > 13) throw new IllegalNotePosition();
                return currentStaffPosition+lowestDownCentrePositionY - n * LINE_GAP - center;
            }
            else {
                positionY-=currentStaffPosition;
                long n = round((lowestUpCentrePositionY - positionY)/LINE_GAP );
                if(n<0 || n>13) throw new IllegalNotePosition();
                return currentStaffPosition+lowestUpCentrePositionY-n*LINE_GAP - center;
            }
    }

    public double getValidSignPosition(double positionY, boolean down, final double LINES_COUNT) throws IllegalNotePosition
    {
            if(down)
            {
                long staffnumber = round(positionY/(STAFF_HEIGHT+STAFF_GAP));
                if(staffnumber<1 || staffnumber>LINES_COUNT) throw new IllegalNotePosition();
                double staffPosition = staffnumber * (STAFF_HEIGHT+STAFF_GAP);
                positionY-=staffPosition;
                long n = round((lowestDownCentrePositionY - positionY) / LINE_GAP);
                if (n < 0 || n > 13) throw new IllegalNotePosition();
                return staffPosition+lowestDownCentrePositionY-n*LINE_GAP - center;
            }
            else
            {
                long staffnumber = round(positionY/(STAFF_HEIGHT+STAFF_GAP));
                if(staffnumber<1 || staffnumber>LINES_COUNT) throw new IllegalNotePosition();
                double staffPosition = staffnumber * (STAFF_HEIGHT+STAFF_GAP);
                positionY-=staffPosition;
                long n = round((lowestUpCentrePositionY - positionY) / LINE_GAP);
                if (n < 0 || n > 13) throw new IllegalNotePosition();
                return staffPosition+lowestUpCentrePositionY-n*LINE_GAP - center;
            }
    }

    public double getValidNotePosition(int noteNumber,boolean down, double currentStaffPosition)
    {
        if(down)
          return  currentStaffPosition+lowestDownCentrePositionY-noteNumber*LINE_GAP - center;
        else
            return currentStaffPosition+lowestUpCentrePositionY-noteNumber*LINE_GAP - center;
    }


}
