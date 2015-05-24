package com.note.quarter;

import javafx.scene.image.ImageView;



public class RestNode extends NoteRestNode {

    public RestNode(Rest rest) {
        noteRest = rest;
        mainImageView = new ImageView(ImageResource.getRestImage(rest.getValue()));
        init();
    }
}
