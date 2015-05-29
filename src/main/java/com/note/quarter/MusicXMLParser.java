package com.note.quarter;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class MusicXMLParser {


    private int division;

    public void open(File f, MusicSheet musicSheet) throws ParserConfigurationException, IOException, SAXException, UnsupportedNotationException {


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(f);
        DocumentType documentType = doc.getDoctype();
        System.out.println(documentType);
        DocumentType check = builder.getDOMImplementation().createDocumentType("score-partwise",
                "-//Recordare//DTD MusicXML 3.0 Partwise//EN",
                "http://www.musicxml.org/dtds/partwise.dtd");
        if(documentType==null || !documentType.getName().equals(check.getName()) ) {throw new IOException();}
        doc.getDocumentElement().normalize();
        readPart(doc,musicSheet);

    }

    private void readPart(Document doc,MusicSheet musicSheet) throws UnsupportedNotationException {
        NodeList nodeList = doc.getElementsByTagName("part");
        if(nodeList.getLength()==0) {throw new UnsupportedNotationException();}
        else
        {
            Element e = null;
            for(int i=0;i<nodeList.getLength();i++)
            {
                if(nodeList.item(i) instanceof Element)
                {
                    e = (Element) nodeList.item(i); break;
                }

            }
            if(e == null) {throw new UnsupportedNotationException();}
            readAttributes(doc,e);
            readMeasures(doc,e,musicSheet);


        }
    }

    private void readAttributes(Document document, Element part) throws UnsupportedNotationException {
        NodeList nodeList = part.getElementsByTagName("attributes");
        Element attributes = null;
        for(int i=0;i<nodeList.getLength();i++)
        {
            if(nodeList.item(i) instanceof Element)
            {
                attributes = (Element) nodeList.item(i);
                System.out.println(attributes.getTagName()); break;
            }
        }
        if(attributes == null) {throw new UnsupportedNotationException();}
        NodeList children = attributes.getChildNodes();
        for(int i=0; i<children.getLength(); i++)
        {

            if(children.item(i) instanceof Element)
            {
                Element element = (Element) children.item(i);
                if(element.getTagName().equals("divisions"))
                {
                    division = Integer.parseInt(element.getTextContent());

                }
                else if(element.getTagName().equals("time"))
                {

                    NodeList beatsList = element.getElementsByTagName("beats");
                    if(beatsList.getLength() == 0 ) {throw new UnsupportedNotationException();}
                    Element beats = (Element) beatsList.item(0);
                    beatsList = element.getElementsByTagName("beat-type");
                    if(beatsList.getLength() ==0) {throw new UnsupportedNotationException(); }
                    Element beatType = (Element) beatsList.item(0);
                    int num = Integer.parseInt(beats.getTextContent());
                    int denom = Integer.parseInt(beatType.getTextContent());
                    if(num!=denom) {throw new UnsupportedNotationException(); }
                }
                else if(element.getTagName().equals("clef"))
                {
                    NodeList signs = element.getElementsByTagName("sign");
                    if(signs.getLength() == 0 ) {throw new UnsupportedNotationException();}
                    Element sign = (Element) signs.item(0);
                    NodeList lines= element.getElementsByTagName("line");
                    if(lines.getLength() ==0) {throw new UnsupportedNotationException();}
                    Element line = (Element)lines.item(0);
                    if(!(line.getTextContent().equals("2")&&sign.getTextContent().equals("G"))){throw new UnsupportedNotationException(); }

                }

            }

        }
    }

    private NoteRestValue computeNoteRestValue(Element note)
    {
        Element dur = (Element) note.getElementsByTagName("duration").item(0);
        int duration = Integer.parseInt(dur.getTextContent());
        int value = (duration * NoteRestValue.QUARTER.getRelativeValue()) / division;
        NoteRestValue val = null;

        for (NoteRestValue v : NoteRestValue.values()) {
            if (v.getRelativeValue() == value) {
                val = v;
                break;
            }
        }
        if (val == null) {
            try {
                throw new UnsupportedNotationException();
            } catch (UnsupportedNotationException e) {
                e.printStackTrace();
            }
        }
        return val;
    }

    private NotePitch computeNotePitch(Element note)
    {
      int midiCode;
        String step = ((Element) note.getElementsByTagName("step").item(0)).getTextContent();
        String octave = ((Element) note.getElementsByTagName("octave").item(0)).getTextContent();

        int index = NotePitch.getIndexFromStep(step);
        int oct = Integer.parseInt(octave);

        String accidental = "";
        if (index <= 2) {
            midiCode = 2 * index;
        } else {
            midiCode = 2 * index - 1;
        }
        midiCode+=readAccidental(note,accidental);
        midiCode += NotePitch.SEMITONES_IN_SCALE * (oct + 1);

        NotePitch pitch = new NotePitch(midiCode);
        pitch.setAccidental(accidental);

        return pitch;

    }

    private int readAccidental(Element note, String accidental)
    {
        if (note.getElementsByTagName("accidental").getLength() != 0) {
            Element acc = (Element) note.getElementsByTagName("accidental").item(0);
            if (acc.getTextContent().equals("sharp")) {
                accidental = "sharp";
                return 1;
            } else if (acc.getTextContent().equals("flat")) {
                accidental = "flat";
                return  -1;
            } else if (acc.getTextContent().equals("natural")) {
                accidental = "natural";
            }

        }
        return 0;
    }

    private void readNotes(Element measure, MusicSheet musicSheet) throws UnsupportedNotationException {
        NodeList notes = measure.getElementsByTagName("note");
        for(int j=0;j<notes.getLength();j++) {
            Element note = (Element) notes.item(j);
            NoteRestValue value = computeNoteRestValue(note);
            if (note.getElementsByTagName("rest").getLength() != 0) {
                Rest rest = new Rest(value);
                musicSheet.addNoteRest(rest);
            } else {

                NotePitch pitch = computeNotePitch(note);
                Note n = new Note(value, pitch);
                musicSheet.addNoteRest(n);
            }
        }
    }

    private void readMeasures(Document doc, Element part, MusicSheet musicSheet) throws UnsupportedNotationException {
        NodeList nodeList = part.getElementsByTagName("measure");
        for(int i=0;i<nodeList.getLength();i++)
        {
            Element measure = (Element) nodeList.item(i);
            readNotes(measure,musicSheet);
        }
    }






}
