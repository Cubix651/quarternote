package com.note.quarter.drawing;

import com.note.quarter.noterest.Note;
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
