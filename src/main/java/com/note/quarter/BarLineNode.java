package com.note.quarter;

import javafx.scene.image.ImageView;

public class BarLineNode extends SheetItem {

    public BarLineNode() {
        mainImageView = new ImageView(ImageResource.getBarLine());
        init();
    }
}
