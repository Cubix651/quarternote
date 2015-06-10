package com.note.quarter.opensave;


import com.note.quarter.drawing.MusicSheet;
import com.note.quarter.noterest.NoteRest;
import javafx.application.Platform;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicXMLParserSAX {

    private MusicSheet musicSheet;

    public MusicXMLParserSAX(MusicSheet musicSheet)
    {
        this.musicSheet = musicSheet;
    }

    public void open(File f) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);

        SAXParser parser = factory.newSAXParser();
        List<NoteRest> noteRestList = new ArrayList<>();
        DefaultHandler handler = new SAXHandler(noteRestList);
        parser.parse(f,handler);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                noteRestList.forEach(musicSheet::addNoteRest);
            }
        });

    }

}
