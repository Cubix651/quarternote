package com.note.quarter.drawing;

import com.note.quarter.noterest.Rest;
import javafx.scene.image.ImageView;

public class RestNode extends NoteRestNode {

    public RestNode(Rest rest) {
        noteRest = rest;
        mainImageView = new ImageView(ImageResource.getRestImage(rest.getValue()));
        init();
    }
}
