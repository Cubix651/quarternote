package com.note.quarter;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class MusicSheet {

    public final double CLEF_POSITION = 0;
    public final double TIME_POSITION = 20;
    private final double barGap = 20;
    public final double STAFF_GAP = 50;
    public final double STAFF_HEIGHT = 50;
    public final double REAL_LINE_GAP=12.5;
    public final double LINE_GAP = REAL_LINE_GAP/2;
    public final double INPUT_LINE_BOUND = LINE_GAP/2;
    private double length;
    private double width;
    public double wholeXGap = 180 + 2;   //to do : change with constructor - deafult =180 - gap for whole note
    public double currentStaffPosition = 100;
    public final double MIN_CURRENT_STAFF_POSITION = STAFF_GAP+STAFF_HEIGHT;
    public final double MIN_CURRENT_X_POSITION = 60;
    public final double MAX_CURRENT_X_POSITION = 850; //width
    public double currentXPosition = MIN_CURRENT_X_POSITION;
    public double currentRelativeXPosition = 0;
   // public final int KEY_COUNT = 14;
    public final int LINES_COUNT;


    private Map<Integer,Double> defaultYDownPositions = new HashMap<>();
    private Map<Integer,Double> defaultYUpPositions = new HashMap<>();
    private Map<String,Integer> nameToDefaultInt = new TreeMap<>();

    private Image staff;
    private Image bar;
    private Canvas canvas;
    private Pane canvasPane;

    public MusicSheet(Canvas canvas, Pane canvasPane, String imageURL)
    {
        length  = canvas.getHeight();
        width = canvas.getWidth();
        staff = new Image(imageURL);

        double y = currentStaffPosition;
        int counter = 0;

        File file  = new File("src/main/resources/com/note/quarter/images/Music-GClef.png");
        File barr = new File("src/main/resources/com/note/quarter/images/bar.png");
        File time = new File("src/main/resources/com/note/quarter/images/commontime.png");

        this.bar = new Image(barr.toURI().toString());

        while(y+STAFF_HEIGHT+STAFF_GAP<length)
        {
            canvas.getGraphicsContext2D().drawImage(staff,0,y,width,STAFF_HEIGHT);
            canvas.getGraphicsContext2D().drawImage(new Image(file.toURI().toString()),CLEF_POSITION,y-25);
            canvas.getGraphicsContext2D().drawImage(new Image(time.toURI().toString()),8,y-25);
            y = y+STAFF_GAP+STAFF_HEIGHT;
            counter++;
        }
        LINES_COUNT = counter;

        this.canvas = canvas;
        this.canvasPane = canvasPane;

        double k = STAFF_HEIGHT/2 + REAL_LINE_GAP + LINE_GAP;


        for(int i=60;i<74;i++)
        {
            defaultYDownPositions.put(i,k);
            defaultYUpPositions.put(i,k+STAFF_HEIGHT/2+REAL_LINE_GAP);
            k=k-LINE_GAP;

        }
        int j = 60;
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



                        if(currentRelativeXPosition+defaultXGap>wholeXGap || currentXPosition+defaultXGap>MAX_CURRENT_X_POSITION )
                        {
                            if(currentRelativeXPosition!=wholeXGap) return;
                            ImageView barImage = new ImageView(bar);
                            barImage.setLayoutX(currentXPosition);
                            barImage.setLayoutY(currentStaffPosition);
                            canvasPane.getChildren().add(barImage);
                            currentXPosition = currentXPosition + barGap;
                            if(currentRelativeXPosition+barGap>wholeXGap)
                                currentRelativeXPosition = 0;
                            if(currentXPosition+barGap>MAX_CURRENT_X_POSITION)
                            {
                                currentXPosition = MIN_CURRENT_X_POSITION;
                                currentStaffPosition = currentStaffPosition+STAFF_HEIGHT+STAFF_GAP;
                                if(currentStaffPosition>length) return;
                            }

                        }


                        if (noteCharacter.equals(NoteCharacter.DOWN)) {
                            double positionY = currentStaffPosition + defaultYDownPositions.get(nameToDefaultInt.get(item.getText()));
                            ImageView imageView = new ImageView(image);
                            imageView.setX(currentXPosition);
                            imageView.setY(positionY - STAFF_HEIGHT / 2);
                            canvasPane.getChildren().add(imageView);
                            currentXPosition = currentXPosition + defaultXGap;
                            currentRelativeXPosition+=defaultXGap;
                        } else if (noteCharacter.equals(NoteCharacter.UP)) {
                            double positionY = currentStaffPosition + defaultYUpPositions.get(nameToDefaultInt.get(item.getText()));
                            ImageView imageView = new ImageView(image);
                            imageView.setX(currentXPosition);
                            imageView.setY(positionY - STAFF_HEIGHT / 2);
                            canvasPane.getChildren().add(imageView);
                            currentXPosition = currentXPosition + wholeXGap * noteValue.getRelativeDuration();
                            currentRelativeXPosition+=defaultXGap;
                        }
                    });
                    getItems().add(item);
                }
            }
        }
        if(noteCharacter.equals(NoteCharacter.REST))
        {
            double defaultXGap = wholeXGap*noteValue.getRelativeDuration();
            if(currentRelativeXPosition+defaultXGap>wholeXGap || currentXPosition+defaultXGap>MAX_CURRENT_X_POSITION )
            {
                if(currentRelativeXPosition!=wholeXGap) return;
                ImageView barImage = new ImageView(bar);
                barImage.setLayoutX(currentXPosition);
                barImage.setLayoutY(currentStaffPosition);
                canvasPane.getChildren().add(barImage);
                currentXPosition = currentXPosition + barGap;
                if(currentRelativeXPosition+barGap>wholeXGap)
                    currentRelativeXPosition = 0;
                if(currentXPosition+barGap>MAX_CURRENT_X_POSITION)
                {
                    currentXPosition = MIN_CURRENT_X_POSITION;
                    currentStaffPosition = currentStaffPosition+STAFF_HEIGHT+STAFF_GAP;
                    if(currentStaffPosition>length) return;
                }

            }


            double positionY = currentStaffPosition;
            ImageView imageView = new ImageView(image);
            imageView.setX(currentXPosition);
            imageView.setY(positionY);
            canvasPane.getChildren().add(imageView);
            currentXPosition = currentXPosition + wholeXGap * noteValue.getRelativeDuration();
            currentRelativeXPosition+=wholeXGap*noteValue.getRelativeDuration();
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

        if(currentRelativeXPosition+defaultXGap>wholeXGap || currentXPosition+defaultXGap>MAX_CURRENT_X_POSITION )
        {
            if(currentRelativeXPosition!=wholeXGap) return;
            ImageView barImage = new ImageView(bar);
            barImage.setLayoutX(currentXPosition);
            barImage.setLayoutY(currentStaffPosition);
            canvasPane.getChildren().add(barImage);
            currentXPosition = currentXPosition + barGap;
            if(currentRelativeXPosition+barGap>wholeXGap)
            currentRelativeXPosition = 0;
            if(currentXPosition+barGap>MAX_CURRENT_X_POSITION)
            {
                currentXPosition = MIN_CURRENT_X_POSITION;
                currentStaffPosition = currentStaffPosition+STAFF_HEIGHT+STAFF_GAP;
                if(currentStaffPosition>length) return;
            }

        }

        double y;
        try{
            if(noteCharacter.equals(NoteCharacter.DOWN))
            y = getValidYPosition(positionY,true);
            else if(noteCharacter.equals(NoteCharacter.UP)) {
                y = getValidYPosition(positionY, false);
            }
            else if(noteCharacter.equals(NoteCharacter.REST))
            {
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
                y = getValidYSignPosition(positionY, true);
            else if(noteCharacter.equals(NoteCharacter.UP)) {
                y = getValidYSignPosition(positionY, false);
            }
            else return;
        } catch (IllegalNotePosition illegalNotePosition) {
            return;
        }
        imageView.setLayoutY(y);
        imageView.setLayoutX(positionX-STAFF_HEIGHT/2);

        canvasPane.getChildren().add(imageView);

    }

    public double getValidYSignPosition(double positionY, boolean Down ) throws IllegalNotePosition
    {
        if(Down)
        {
            for(double d = MIN_CURRENT_STAFF_POSITION; d<=currentStaffPosition; d+=STAFF_GAP+STAFF_HEIGHT) {
                for (Double defPos : defaultYDownPositions.values()) {
                    if(d+defPos-INPUT_LINE_BOUND<=positionY && d+defPos+INPUT_LINE_BOUND>=positionY) {

                        return d + defPos -STAFF_HEIGHT/2;
                    }
                }
            }
        }
        else
        {
            for(double d = MIN_CURRENT_STAFF_POSITION; d<=currentStaffPosition; d+=STAFF_GAP+STAFF_HEIGHT) {
                for (Double defPos : defaultYUpPositions.values()) {
                    if(d+defPos-INPUT_LINE_BOUND<=positionY && d+defPos+INPUT_LINE_BOUND>=positionY) {

                        return d + defPos -STAFF_HEIGHT/2;
                    }
                }
            }
        }
        throw new IllegalNotePosition();
    }


    public double getValidYPosition(double positionY, boolean Down) throws IllegalNotePosition
    {
        if(Down)
        for(Double defPos : defaultYDownPositions.values())
        {

            if(currentStaffPosition+defPos-INPUT_LINE_BOUND<=positionY && currentStaffPosition+defPos+INPUT_LINE_BOUND>=positionY) {

                return currentStaffPosition + defPos -STAFF_HEIGHT/2;
            }
        }
        else
        {
            for(Double defPos : defaultYUpPositions.values())
            {

                if(currentStaffPosition+defPos-INPUT_LINE_BOUND<=positionY && currentStaffPosition+defPos+INPUT_LINE_BOUND>=positionY) {
                    System.out.println(currentStaffPosition+defPos);
                    return currentStaffPosition + defPos -STAFF_HEIGHT/2;
                }
            }
        }


        throw new IllegalNotePosition();
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public Image getStaffImage() {
        return staff;
    }
}
