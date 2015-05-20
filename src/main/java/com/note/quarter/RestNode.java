package com.note.quarter;

import javafx.scene.image.ImageView;

public class RestNode extends NoteRestNode {
    private Rest rest;

    public RestNode(Rest rest) {
        this.rest = rest;
        mainImageView = new ImageView(ImageResource.getRestImage(rest.getValue()));
        init();
    }
}
