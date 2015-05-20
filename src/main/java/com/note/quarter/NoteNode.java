package com.note.quarter;

import javafx.scene.image.ImageView;

public class NoteNode extends NoteRestNode {

    public NoteNode(Note note) {
        noteRest = note;
        if(note.getPitch().withSharp())
            mainImageView = new ImageView(ImageResource.getSharpNoteImage(note.getValue()));
        else
            mainImageView = new ImageView(ImageResource.getUpNoteImage(note.getValue()));
        init();
    }
}
