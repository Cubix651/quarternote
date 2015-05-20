package com.note.quarter;

import javafx.scene.image.ImageView;

public class NoteNode extends NoteRestNode {

    public NoteNode(Note note) {
        noteRest = note;
        mainImageView = new ImageView(ImageResource.getUpNoteImage(note.getValue()));
        init();
    }
}
