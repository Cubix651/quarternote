package com.note.quarter;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Alerts{

    public static Alert raiseErrorAlert(Exception e, String additionalInformation)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error occurred: ");
        alert.setContentText(e +" " +additionalInformation);
        return alert;
    }
    public static Alert raiseConfirmationAlert(String header, String content)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
    public static Alert raiseConfirmOpeningAlert()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to start a new project or append new file to the existing one?",ButtonType.CANCEL);
        alert.getButtonTypes().add(new ButtonType("APPEND"));
        alert.getButtonTypes().add(new ButtonType("NEW PROJECT"));
        alert.setHeaderText("You have unsaved changes");
        return alert;
    }

}
