package com.note.quarter.opensave;


import com.note.quarter.drawing.MusicSheet;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


public class OpenTask extends Task{

    private Label label;
    private MusicSheet sheet;
    private File f;
    private StackPane pane;
    public OpenTask(Label label, MusicSheet musicSheet, File file, StackPane pane) {
        this.label = label;
        sheet = musicSheet;
        f = file;
        this.pane = pane;
    }

    @Override
    protected Object call() throws Exception {


        MusicXMLParserSAX parserSAX = new MusicXMLParserSAX(sheet);
        setProgressIndicator(label);
        try{
            parserSAX.open(f);
        } catch (ParserConfigurationException e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alerts.raiseErrorAlert(e, "Parser configuration exception").showAndWait();
                }
            });
            e.printStackTrace();
        } catch (SAXException e) {
            Exception exception = e.getException();
            if (exception == null) {
                Platform.runLater(new Runnable() {
                @Override
                    public void run() {
                        Alerts.raiseErrorAlert(e, "SAX Exception").showAndWait();
                    }
                });
            }
            else if (exception instanceof UnsupportedNotationException) {
                Platform.runLater(new Runnable() {
                @Override
                    public void run() {
                        Alerts.raiseErrorAlert(exception, "Cannot resolve notation").showAndWait();
                    }
                });
            }
            else if (exception instanceof IllegalDocumentTypeException) {
                Platform.runLater(new Runnable() {
                @Override
                    public void run() {
                    Alerts.raiseErrorAlert(exception, "Invalid document type").showAndWait();
                }
                });

            }
            e.printStackTrace();
        } catch (IOException e) {
                Platform.runLater(new Runnable() {
                @Override
                    public void run() {
                    Alerts.raiseErrorAlert(e, "IO exception").showAndWait();
                    }
                });
            e.printStackTrace();
        } finally {
            updateLabel(label);
        }
            return null;
    }

    public void updateLabel(Label im)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.getChildren().remove(label);
            }
        });
    }
    private void setProgressIndicator(final Label v)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                v.setGraphic(new ProgressIndicator());
            }
        });

    }
}


