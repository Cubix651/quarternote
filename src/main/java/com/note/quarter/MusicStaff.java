package com.note.quarter;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.LinkedList;

import static java.lang.Math.round;


public class MusicStaff {

    public final double STAFF_HEIGHT;
    public final double GAP_BETWEEN_STAFFS;
    public final double GAP_BETWEEN_PITCHES;
    public final double NUMBER_OF_HIGHEST_NOTE_IN_SCALE = 13;
    public final NotePitch LOWEST_PITCH = new NotePitch(60);
    public final double MEASURE_MAX_WIDTH = 300;

    public final double CLEF_X_POSITION = 0;
    public final double METER_X_POSITION = 5;

    private double lowestPositionY;
    private double currentStaffPosition;

    public final double MIN_X_POSITION = 80;
    public final double MAX_X_POSITION = ImageResource.getStaff().getWidth();
    public double currentXPosition = MIN_X_POSITION;
    public int remainingUnits = NoteRestValue.WHOLE.getRelativeValue();


    private Canvas canvas;
    private Pane canvasPane;

    private LinkedList<SheetItem> nodes = new LinkedList<>();

    public LinkedList<SheetItem> getNodes() {
        return nodes;
    }

    public MusicStaff(Canvas canvas, Pane canvasPane)
    {
        STAFF_HEIGHT = ImageResource.getStaff().getHeight();
        GAP_BETWEEN_STAFFS = STAFF_HEIGHT;
        GAP_BETWEEN_PITCHES = STAFF_HEIGHT/8;

        lowestPositionY = STAFF_HEIGHT + 2 * GAP_BETWEEN_PITCHES;

        currentStaffPosition = GAP_BETWEEN_STAFFS;
        drawStaff(canvas);

        this.canvas = canvas;
        this.canvasPane = canvasPane;
    }

    public void drawStaff(Canvas canvas)
    {
        double sheetHeight = canvas.getHeight();
        double y = GAP_BETWEEN_STAFFS;
        while(y+STAFF_HEIGHT+ GAP_BETWEEN_STAFFS <sheetHeight)
        {
            canvas.getGraphicsContext2D().drawImage(ImageResource.getStaff(),0,y);
            Image clef = ImageResource.getClef();
            canvas.getGraphicsContext2D().drawImage(clef, CLEF_X_POSITION, y - (clef.getHeight() - STAFF_HEIGHT) / 2);
            Image meter = ImageResource.getMeter();
            canvas.getGraphicsContext2D().drawImage(meter, METER_X_POSITION,y - (meter.getHeight() - STAFF_HEIGHT) / 2);
            y += GAP_BETWEEN_STAFFS + STAFF_HEIGHT;
        }
    }

    public NotePitch computePitchFromYPosition(double y) {
        double relativeY = currentStaffPosition + lowestPositionY - y;
        int n = (int) round(relativeY / GAP_BETWEEN_PITCHES);
        if (n < 0 || n > NUMBER_OF_HIGHEST_NOTE_IN_SCALE) return null;
        return new NotePitch(LOWEST_PITCH.getIndex() + n, false);
    }

    public double computeYPositionFromPitch(NotePitch pitch) {
        double y = currentStaffPosition + lowestPositionY;
        y -= (pitch.getIndex() - LOWEST_PITCH.getIndex()) * GAP_BETWEEN_PITCHES;
        return y;
    }

    private boolean isNextMeasureInRowPossible() {
        if(currentXPosition + MEASURE_MAX_WIDTH <= MAX_X_POSITION)
            return true;
        return false;
    }

    private void gotoNextRow() {
        currentXPosition = MIN_X_POSITION;
        currentStaffPosition += STAFF_HEIGHT + GAP_BETWEEN_STAFFS;
    }

    private boolean isBarLineNecessary() {
        return (remainingUnits == 0);
    }

    private void insertSheetItem(SheetItem item, double x, double y) {
        item.setLayoutX(x);
        item.setLayoutY(y);
        canvasPane.getChildren().add(item);
        nodes.add(item);
        currentXPosition += item.getWidth();
    }

    private void insertBarLine() {
        insertSheetItem(new BarLineNode(), currentXPosition, currentStaffPosition);
        remainingUnits = NoteRestValue.WHOLE.getRelativeValue();
    }

    private boolean enoughUnitsForNoteRest(NoteRestValue value) {
        return value.getRelativeValue() <= remainingUnits;
    }

    public void drawNoteRest(NoteRest noteRest) {
        if(!enoughUnitsForNoteRest(noteRest.getValue()))
            return;

        NoteRestNode noteRestNode;
        double y;
        if (noteRest.isNote()) {
            Note note = (Note) noteRest;
            noteRestNode = new NoteNode(note);
            y = computeYPositionFromPitch(note.getPitch()) - noteRestNode.getHeight() / 2;
        } else {
            Rest rest = (Rest) noteRest;
            noteRestNode = new RestNode(rest);
            y = currentStaffPosition;
        }

        /****************HERE*****************/
        insertSheetItem(noteRestNode, currentXPosition, y);
        remainingUnits -= noteRest.getValue().getRelativeValue();

        if(isBarLineNecessary()) {
            insertBarLine();
            if(!isNextMeasureInRowPossible())
                gotoNextRow();
        }
    }

    private void eraseLastSheetItem() {
        SheetItem item = nodes.getLast();
        canvasPane.getChildren().remove(item);
        nodes.removeLast();
        currentXPosition -= item.getWidth();
        if(item instanceof NoteRestNode) {
            remainingUnits += ((NoteRestNode) item).getNoteRest().getValue().getRelativeValue();
        }
        else if(item instanceof BarLineNode) {
            if(currentXPosition < MIN_X_POSITION) {
                currentXPosition = nodes.getLast().getLayoutX() + nodes.getLast().getWidth();
                currentStaffPosition -= STAFF_HEIGHT + GAP_BETWEEN_STAFFS;
            }
            remainingUnits = 0;
        }
    }

    public void eraseLastNoteRest() {
        while(!nodes.isEmpty() && nodes.getLast() instanceof BarLineNode)
            eraseLastSheetItem();
        if(!nodes.isEmpty())
            eraseLastSheetItem();
    }

}
