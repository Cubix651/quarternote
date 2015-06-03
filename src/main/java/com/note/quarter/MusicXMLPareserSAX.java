package com.note.quarter;


import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

public class MusicXMLPareserSAX {

    private MusicSheet musicSheet;

    public MusicXMLPareserSAX(MusicSheet musicSheet)
    {
        this.musicSheet = musicSheet;
    }

    public void open(File f) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        DefaultHandler handler = new SAXHandler(musicSheet);
        InputStream inputStream = new FileInputStream(f);
        parser.parse(f,handler);

    }

}
