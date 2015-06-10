package com.note.quarter.controls;

import javafx.scene.control.TextField;

public class NumericTextField extends TextField {
    @Override
    public void replaceText(int start, int end, String t) {
        if (validate(t)) {
            super.replaceText(start, end, t);
        }
    }

    @Override
    public void replaceSelection(String t) {
        if (validate(t)) {
            super.replaceSelection(t);
        }
    }

    private boolean validate(String t) {
        return t.matches("[0-9]*");
    }

    public int getValue() {
        if(this.getText().equals(""))
            return 0;
        return Integer.parseInt(this.getText());
    }
}
