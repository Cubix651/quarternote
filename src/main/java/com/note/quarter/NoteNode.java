package com.note.quarter;

import javafx.scene.image.ImageView;

public class NoteNode extends NoteRestNode {
    private Note note;

    public NoteNode(Note note) {
        this.note = note;
        mainImageView = new ImageView(ImageResource.getUpNoteImage(note.getValue()));
        init();
    }
}
