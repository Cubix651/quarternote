package com.note.quarter;

import javafx.scene.control.TextField;

/**
 * Created by stella on 16.05.15.
 */
public class BPMTextField extends TextField {
    @Override
    public void replaceText(int start, int end, String t)
    {
        if(isInteger(t))
        {
            super.replaceText(start,end,t);
        }
    }

    @Override
    public void replaceSelection(String t)
    {
        if(isInteger(t))
        {
            super.replaceSelection(t);
        }
    }
    private boolean isInteger(String t)
    {
        return (t.equals("") || t.matches("[0-9]") );

    }


}
