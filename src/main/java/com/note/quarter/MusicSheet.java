package com.note.quarter;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Map;
import java.util.TreeMap;


public class MusicSheet {

    private final double barGap = 20;
    private double length;
    private double width;
    public double wholeXGap = 180 + 2;   //to do : change with constructor - deafult =180 - gap for whole note
    public double currentStaffPosition ;
    public final double MIN_CURRENT_STAFF_POSITION;
    public final double MIN_CURRENT_X_POSITION = 60;
    public final double MAX_CURRENT_X_POSITION = 850; //width
    public double currentXPosition = MIN_CURRENT_X_POSITION;
    public double currentRelativeXPosition = 0;
    public final int LINES_COUNT;

    private Map<String,Integer> nameToDefaultInt = new TreeMap<>();

    private Image bar;
    private MusicStaff musicStaff;
    private Canvas canvas;
    private Pane canvasPane;

    public MusicSheet(Canvas canvas, Pane canvasPane)
    {
        String staffPath = getClass().getResource("images/Music-staff-small.png").toString();
        String clefPath = getClass().getResource("images/Music-GClef.png").toString();
        String barPath = getClass().getResource("images/bar.png").toString();
        String meterPath = getClass().getResource("images/commontime.png").toString();

        musicStaff = new MusicStaff(new Image(staffPath),new Image(clefPath),new Image(meterPath));

        length  = canvas.getHeight();
        width = canvas.getWidth();

        MIN_CURRENT_STAFF_POSITION = musicStaff.STAFF_HEIGHT+musicStaff.STAFF_GAP;
        currentStaffPosition = MIN_CURRENT_STAFF_POSITION;

        this.bar = new Image(barPath);

        LINES_COUNT = musicStaff.drawStaff(canvas,length,width,8);
        this.canvas = canvas;
        this.canvasPane = canvasPane;

        int j = 0;
        for(int i=67;i<72;i++)
        {
            String s = new String(Character.toChars(i));
            s += "4";
            nameToDefaultInt.put(s,j);
            j++;
        }
        String s = new String(Character.toChars(65));
        s += "4";
        nameToDefaultInt.put(s,j);
        j++;
        s = new String(Character.toChars(72));
        s+="4";
        j++;
        nameToDefaultInt.put(s,j);
        j++;
        for(int i=67;i<72;i++)
        {
            s = new String(Character.toChars(i));
            s += "5";
            nameToDefaultInt.put(s,j);
            j++;
        }
        s = new String(Character.toChars(65));
        s += "5";
        nameToDefaultInt.put(s,j);
        j++;
        s = new String(Character.toChars(72));
        s+="5";
        j++;
        nameToDefaultInt.put(s,j);
    }

    private boolean checkAndUpdateMeter(double defaultXGap)
    {

        if(currentRelativeXPosition+defaultXGap>wholeXGap || currentXPosition+defaultXGap>MAX_CURRENT_X_POSITION )
        {
            if(currentRelativeXPosition!=wholeXGap) return false;  //it illegal to insert a note;
            ImageView barImage = new ImageView(bar); //add bar
            barImage.setLayoutX(currentXPosition);
            barImage.setLayoutY(currentStaffPosition);
            canvasPane.getChildren().add(barImage);
            currentXPosition = currentXPosition + barGap;

            if(currentRelativeXPosition+barGap>wholeXGap)
                currentRelativeXPosition = 0;
            if(currentXPosition+barGap>MAX_CURRENT_X_POSITION)
            {
                currentXPosition = MIN_CURRENT_X_POSITION;
                currentStaffPosition = currentStaffPosition+musicStaff.STAFF_HEIGHT+musicStaff.STAFF_GAP;
                if(currentStaffPosition>length) return false; //no more place to insert
            }

        }
        return true;
    }



    public void setNote(Image image,NoteValue noteValue, NoteCharacter noteCharacter, Button button, double x, double y)
    {

        class PopupMenu extends ContextMenu{
            public PopupMenu()
            {
                for(String s : nameToDefaultInt.keySet()) {
                    MenuItem item = new MenuItem();
                    item.setText(s);
                    item.setOnAction(event -> {


                        double defaultXGap = wholeXGap*noteValue.getRelativeDuration();
                        int positionNumber = nameToDefaultInt.get(item.getText());
                        if(!checkAndUpdateMeter(defaultXGap)) return;

                        double y;
                        if(noteCharacter.equals(NoteCharacter.DOWN))
                            y = musicStaff.getValidNotePosition(positionNumber,true,currentStaffPosition);
                        else if(noteCharacter.equals(NoteCharacter.UP))
                            y = musicStaff.getValidNotePosition(positionNumber,false,currentStaffPosition);
                        else return;

                        ImageView imageView = new ImageView(image);
                        imageView.setX(currentXPosition);
                        imageView.setY(y);
                        canvasPane.getChildren().add(imageView);

                        currentXPosition = currentXPosition + defaultXGap;
                        currentRelativeXPosition+=defaultXGap;

                    });
                    getItems().add(item);
                }
            }
        }
        if(noteCharacter.equals(NoteCharacter.REST))
        {
            double defaultXGap = wholeXGap*noteValue.getRelativeDuration();
            if(!checkAndUpdateMeter(defaultXGap)) return;
            double positionY = currentStaffPosition;
            ImageView imageView = new ImageView(image);
            imageView.setX(currentXPosition);
            imageView.setY(positionY);
            canvasPane.getChildren().add(imageView);
            currentXPosition = currentXPosition + defaultXGap;
            currentRelativeXPosition+=defaultXGap;
        }
        else {
            PopupMenu popupMenu = new PopupMenu();
            popupMenu.show(button, x, y);
        }
    }

    public void setValidNote(Image image, double positionY, NoteValue noteValue, NoteCharacter noteCharacter)
    {
        ImageView imageView = new ImageView(image);
        double defaultXGap = wholeXGap*noteValue.getRelativeDuration();

        if(!checkAndUpdateMeter(defaultXGap)) return;

        double y;
        try{
            if(noteCharacter.equals(NoteCharacter.DOWN))
                y = musicStaff.getValidNotePosition(positionY, true, currentStaffPosition);
            else if(noteCharacter.equals(NoteCharacter.UP)) {
                y = musicStaff.getValidNotePosition(positionY, false, currentStaffPosition);
            }
            else if(noteCharacter.equals(NoteCharacter.REST)) {
                y = currentStaffPosition;
            }
            else return;
        } catch (IllegalNotePosition illegalNotePosition) {
            return;
        }

        imageView.setLayoutY(y);
        imageView.setLayoutX(currentXPosition);
        currentXPosition+=defaultXGap;
        currentRelativeXPosition+=defaultXGap;
        canvasPane.getChildren().add(imageView);
    }


    public void setValidSign(Image image, double positionY,double positionX, NoteCharacter noteCharacter){

        ImageView imageView = new ImageView(image);
        double y;
        try {
            if(noteCharacter.equals(NoteCharacter.DOWN))
                y = musicStaff.getValidSignPosition(positionY, true, LINES_COUNT);
            else if(noteCharacter.equals(NoteCharacter.UP)) {
                y = musicStaff.getValidSignPosition(positionY, false,LINES_COUNT);
            }
            else return;
        } catch (IllegalNotePosition illegalNotePosition) {
            return;
        }
        imageView.setLayoutY(y);
        imageView.setLayoutX(positionX-musicStaff.STAFF_HEIGHT/2);
        canvasPane.getChildren().add(imageView);

    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

}
