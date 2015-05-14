package com.note.quarter;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageResource {
    private static Map<NoteRestValue, Image> upNotes = new HashMap<>();
    private static Map<NoteRestValue, Image> downNotes = new HashMap<>();
    private static Map<NoteRestValue, Image> rests = new HashMap<>();

    private static Image staff = new Image(ImageResource.class.getResource("images/staff.png").toString());
    private static Image clef = new Image(ImageResource.class.getResource("images/gclef.png").toString());
    private static Image bar = new Image(ImageResource.class.getResource("images/bar.png").toString());
    private static Image meter = new Image(ImageResource.class.getResource("images/commontime.png").toString());
    
    static {
        for(NoteRestValue value : NoteRestValue.values()) {
            String name = value.name().toLowerCase();
            upNotes.put(value, new Image(ImageResource.class.getResource("images/notes/up/" + name + ".png").toString()));
            downNotes.put(value, new Image(ImageResource.class.getResource("images/notes/down/" + name + ".png").toString()));
            rests.put(value, new Image(ImageResource.class.getResource("images/rests/" + name + ".png").toString()));
        }
    }

    public static Image getUpNoteImage(NoteRestValue value) {
        return upNotes.get(value);
    }

    public static Image getDownNoteImage(NoteRestValue value) {
        return downNotes.get(value);
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

    public static Image getBar() {
        return bar;
    }

    public static Image getMeter() {
        return meter;
    }
}
