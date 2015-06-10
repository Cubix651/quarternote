package com.note.quarter.opensave;

import com.note.quarter.drawing.BarLineNode;
import com.note.quarter.drawing.NoteRestNode;
import com.note.quarter.drawing.SheetItem;
import com.note.quarter.noterest.Note;
import com.note.quarter.noterest.NoteRest;
import com.note.quarter.noterest.Rest;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class MusicXMLBuilder {

    public void save(List<SheetItem> notes, File f)
    {
        try {
            Document doc = createDocument(notes);
            write(doc, f);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void write(Document doc, File f) throws TransformerConfigurationException {
        TransformerFactory  factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMImplementation domImplementation = doc.getImplementation();

        DocumentType documentType = domImplementation.createDocumentType("score-partwise",
                "-//Recordare//DTD MusicXML 3.0 Partwise//EN",
                "http://www.musicxml.org/dtds/partwise.dtd");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
        transformer.setOutputProperty(OutputKeys.METHOD,"xml");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,documentType.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,documentType.getSystemId());
        doc.appendChild(documentType);


        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(f);
        try {
            transformer.transform(source,result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private Document createDocument(List<SheetItem> notes) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        setRoot(doc);
        if(!notes.isEmpty()) {
            setPart(doc);
            writeNotes(notes, doc);
        }
        return doc;
    }

    private void setDivision(Document doc, Element attributes)
    {
        Element divisions = doc.createElement("divisions");
        Text divisionsText = doc.createTextNode("2");
        divisions.appendChild(divisionsText);
        attributes.appendChild(divisions);
    }

    private void setKey(Document doc, Element attributes)
    {
        Element key = doc.createElement("key");
        Element fifths = doc.createElement("fifths");
        Text fifthsText = doc.createTextNode("0");
        fifths.appendChild(fifthsText);
        key.appendChild(fifths);
        attributes.appendChild(key);
    }

    private void setTime(Document doc, Element attributes)
    {
        Element time = doc.createElement("time");
        Element beats = doc.createElement("beats");
        Element beatType = doc.createElement("beat-type");
        Attr attr = doc.createAttribute("symbol");
        attr.setValue("common");
        time.setAttributeNode(attr);
        Text beatsText = doc.createTextNode("4");
        Text beatTypeText = doc.createTextNode("4");
        beats.appendChild(beatsText);
        beatType.appendChild(beatTypeText);
        time.appendChild(beats);
        time.appendChild(beatType);
        attributes.appendChild(time);
    }

    private void setClef(Document doc, Element attributes)
    {
        Element clef = doc.createElement("clef");
        Element sign = doc.createElement("sign");
        Element line = doc.createElement("line");
        Text signText = doc.createTextNode("G");
        Text lineText = doc.createTextNode("2");
        sign.appendChild(signText);
        line.appendChild(lineText);
        clef.appendChild(sign);
        clef.appendChild(line);
        attributes.appendChild(clef);

    }

    private void setAttributes(Document doc, Element measure)
    {
        Element attributes = doc.createElement("attributes");
        setDivision(doc,attributes);
        setKey(doc,attributes);
        setTime(doc,attributes);
        setClef(doc,attributes);
        measure.appendChild(attributes);
    }

    private void writeNotes(List<SheetItem> notes, Document doc) {

        Element root = doc.getDocumentElement();
        Element part = doc.createElement("part");
        Attr attr = doc.createAttribute("id");
        attr.setValue("P1");
        part.setAttributeNode(attr);
        root.appendChild(part);

        int m = 1;
        Element measure = doc.createElement("measure");
        attr = doc.createAttribute("number");
        attr.setValue(m+"");
        measure.setAttributeNode(attr);
        part.appendChild(measure);
        m++;

        setAttributes(doc, measure);


        for(SheetItem item : notes)
        {
            if(item instanceof BarLineNode)
            {
                measure = doc.createElement("measure");
                attr = doc.createAttribute("number");
                attr.setValue(m+"");
                measure.setAttributeNode(attr);
                part.appendChild(measure);
                m++;
            }
            if(item instanceof NoteRestNode)
            {
                NoteRestNode node = (NoteRestNode)item;
                NoteRest noteRest = node.getNoteRest();
                if(noteRest instanceof Note)
                {
                    addNote(measure,(Note)noteRest,doc);
                }
                if(noteRest instanceof Rest)
                {
                    addRest(measure,(Rest)noteRest,doc);
                }
            }
        }


    }


    private void addNote(Element measure, Note n, Document doc)
    {
        Element note = doc.createElement("note");
        Element pitch = doc.createElement("pitch");
        Element step = doc.createElement("step");
        Element octave = doc.createElement("octave");
        Element duration = doc.createElement("duration");
        Element type = doc.createElement("type");


        Text stepText = doc.createTextNode(n.getPitch().getStep());
        Text octaveText = doc.createTextNode(n.getPitch().getOctave()+"");
        Text durationText = doc.createTextNode(n.getValue().getRelativeValue() +"");
        Text typeText = doc.createTextNode(n.getValue().toString().toLowerCase());

        type.appendChild(typeText);
        duration.appendChild(durationText);
        octave.appendChild(octaveText);
        step.appendChild(stepText);
        pitch.appendChild(step);
        pitch.appendChild(octave);
        note.appendChild(pitch);
        note.appendChild(duration);
        note.appendChild(type);

        if(n.getPitch().getAccidental()!=null)
        {

            Element accidental = doc.createElement("accidental");
            Text accidentalText = doc.createTextNode(n.getPitch().getAccidental());
            accidental.appendChild(accidentalText);
            note.appendChild(accidental);
        }

        measure.appendChild(note);

    }

    private void addRest(Element measure, Rest n, Document doc)
    {
        Element note = doc.createElement("note");
        Element rest = doc.createElement("rest");
        Element duration = doc.createElement("duration");
        Element type = doc.createElement("type");

        Text durationText = doc.createTextNode(n.getValue().getRelativeValue() +"");
        Text typeText = doc.createTextNode(n.getValue().toString().toLowerCase());

        type.appendChild(typeText);
        duration.appendChild(durationText);
        note.appendChild(rest);
        note.appendChild(duration);
        note.appendChild(type);
        measure.appendChild(note);
    }

    private void setRoot(Document doc)
    {

        Element element = doc.createElement("score-partwise");
        Attr attr = doc.createAttribute("version");
        attr.setValue("3.0");
        element.setAttributeNode(attr);
        doc.appendChild(element);

    }

    private void setPart(Document doc)
    {
        Element root = doc.getDocumentElement();
        Element parts = doc.createElement("part-list");
        Element scorePart = doc.createElement("score-part");
        Element partName = doc.createElement("part-name");
        Text name = doc.createTextNode("Part 1");

        Attr scoreAttr = doc.createAttribute("id");
        scoreAttr.setValue("P1");
        scorePart.setAttributeNode(scoreAttr);

        partName.appendChild(name);
        scorePart.appendChild(partName);
        parts.appendChild(scorePart);
        root.appendChild(parts);

    }


}
