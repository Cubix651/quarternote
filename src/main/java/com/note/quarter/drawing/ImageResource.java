package com.note.quarter.drawing;

import com.note.quarter.noterest.NoteRestValue;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageResource {
    private static Map<NoteRestValue, Image> upNotes = new HashMap<>();
    private static Map<NoteRestValue, Image> downNotes = new HashMap<>();
    private static Map<NoteRestValue, Image> sharpNotes = new HashMap<>();
    private static Map<NoteRestValue, Image> rests = new HashMap<>();
    private static Map<NoteRestValue, Image> additionals = new HashMap<>();

    private static Image staff = new Image(ImageResource.class.getResource("../images/staff.png").toString());
    private static Image clef = new Image(ImageResource.class.getResource("../images/gclef.png").toString());
    private static Image barLine = new Image(ImageResource.class.getResource("../images/barLine.png").toString());
    private static Image meter = new Image(ImageResource.class.getResource("../images/commontime.png").toString());


    static {
        for(NoteRestValue value : NoteRestValue.values()) {
            String name = value.name().toLowerCase();
            upNotes.put(value, new Image(ImageResource.class.getResource("../images/notes/up/" + name + ".png").toString()));
            downNotes.put(value, new Image(ImageResource.class.getResource("../images/notes/down/" + name + ".png").toString()));
            sharpNotes.put(value, new Image(ImageResource.class.getResource("../images/notes/sharp/" + name + ".png").toString()));
            rests.put(value, new Image(ImageResource.class.getResource("../images/rests/" + name + ".png").toString()));
            additionals.put(value,new Image(ImageResource.class.getResource("../images/additionals/"+"additional_"+name+".png").toString()));
        }
    }

    public static Image getAdditional(NoteRestValue value){
        return additionals.get(value);
    }

    public static Image getUpNoteImage(NoteRestValue value) {
        return upNotes.get(value);
    }

    public static Image getDownNoteImage(NoteRestValue value) {
        return downNotes.get(value);
    }

    public static Image getSharpNoteImage(NoteRestValue value) {
        return sharpNotes.get(value);
    }

    public static Image getRestImage(NoteRestValue value) {
        return rests.get(value);
    }

    public static Image getStaff() {
        return staff;
    }

    public static Image getClef() {
        return clef;
    }

    public static Image getBarLine() {
        return barLine;
    }

    public static Image getMeter() {
        return meter;
    }
}
