package com.note.quarter;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Alerts{

    public static Alert raiseErrorAlert(Exception e, String additionalInformation)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error occurred: ");
        alert.setContentText(e.getClass().getSimpleName() +"\n" +additionalInformation);
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"You can start a new project or append the file", ButtonType.CANCEL);
        alert.getButtonTypes().add(new ButtonType("APPEND"));
        alert.getButtonTypes().add(new ButtonType("NEW"));
        alert.setHeaderText("Current project is not empty");
        return alert;
    }

}
