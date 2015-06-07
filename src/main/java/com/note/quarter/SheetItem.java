package com.note.quarter;

import javafx.scene.Group;
import javafx.scene.image.ImageView;



public abstract class SheetItem extends Group {

    protected ImageView mainImageView;

    protected void init() {
        getChildren().add(mainImageView);
    }

    public double getWidth() {
        return mainImageView.getImage().getWidth();
    }

    public double getHeight() {
        return mainImageView.getImage().getHeight();
    }

    public double getX() {return mainImageView.getX();}

    public double getY() {return mainImageView.getY();

    }
}
