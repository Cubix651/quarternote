package com.note.quarter;

import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Created by stella on 17.05.15.
 */
public class TimeButton extends Button {


    private long pressedTime;

    public long getPressedTime() {
        return pressedTime;
    }

    public void setPressedTime(long pressedTime) {
        this.pressedTime = pressedTime;
    }

    public TimeButton()
    {
        super();
    }
    public TimeButton(String text)
    {
        super(text);
    }
    public TimeButton(String text, Node graphic)
    {
        super(text,graphic);
    }

}
