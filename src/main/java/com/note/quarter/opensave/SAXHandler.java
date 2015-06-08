package com.note.quarter.opensave;

import com.note.quarter.drawing.MusicSheet;
import com.note.quarter.noterest.Note;
import com.note.quarter.noterest.NotePitch;
import com.note.quarter.noterest.NoteRestValue;
import com.note.quarter.noterest.Rest;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;


public class SAXHandler extends DefaultHandler {

    private Stack<String> elements = new Stack<>();
    private MusicSheet musicSheet;
    private int currentBeat;
    private int currentBeatType;
    private int divisions;
    private boolean currentClefSet;
    private NotePitch currentNotePitch;
    private NoteRestValue currentNoteRestValue;
    private int currentNoteIndex;
    private int currentNoteOctave;



    public SAXHandler(MusicSheet musicSheet){
        this.musicSheet = musicSheet;
    }

    public void startDocument() throws SAXException {}


    public void endDocument() throws SAXException {}

    public void startElement(String uri, String localName, String qName, Attributes var4) throws SAXException {

        System.out.println("processing " + qName);
        elements.push(qName);
        if(qName.equals("clef"))
        {
            currentClefSet = true;
        }


    }

    public void endElement(String var1, String var2, String var3) throws SAXException {

        String popped = elements.pop();
        if(popped.equals("note")){
           if(currentNotePitch!=null)
           {
               Note n = new Note(currentNoteRestValue,currentNotePitch);
               musicSheet.addNoteRest(n);
               currentNotePitch = null;
               currentNoteRestValue = null;
           }
            else {//is a rest
               Rest rest = new Rest(currentNoteRestValue);
               musicSheet.addNoteRest(rest);
               currentNotePitch = null;
           }
        }
        if(popped.equals("clef")){
            currentClefSet = false;
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {

        String expression = new String(ch,start,length).trim();

        //attributes
        if(elements.peek().equals("divisions"))
        {
            divisions = Integer.parseInt(expression);
        }
        else if(elements.peek().equals("beats"))
        {
            currentBeat = Integer.parseInt(expression);
        }
        else if(elements.peek().equals("beat-type"))
        {
            currentBeatType = Integer.parseInt(expression);
            if(currentBeatType == 0 || currentBeat == 0) throw new SAXException(new UnsupportedNotationException());
            if(currentBeat!=currentBeatType) throw new SAXException(new UnsupportedNotationException());
        }
        else if(elements.peek().equals("sign"))
        {
            if(currentClefSet)
            {
                if(!expression.equals("G")) throw new SAXException(new UnsupportedNotationException());

            }
        }
        else if(elements.peek().equals("line"))
        {
            if(currentClefSet)
            {
                if(!expression.equals("2")) throw new SAXException(new UnsupportedNotationException());
            }
        }
        else if(elements.peek().equals("fifths"))
        {
            if(!expression.equals("0")) throw new SAXException(new UnsupportedNotationException());
        }

        //notes
        else if(elements.peek().equals("step"))
        {
            currentNoteIndex = NotePitch.getIndexFromStep(expression);
        }
        else if(elements.peek().equals("octave"))
        {
            currentNoteOctave = Integer.parseInt(expression);
            currentNotePitch = computeNotePitch();
        }
        else if(elements.peek().equals("accidental"))
        {
            if(expression.equals("flat"))
            {
                currentNotePitch.setMidiCode(currentNotePitch.getMidiCode()-1);
                currentNotePitch.setAccidental(expression);
            }
            else if(expression.equals("sharp"))
            {
                currentNotePitch.setMidiCode(currentNotePitch.getMidiCode()+1);
                currentNotePitch.setAccidental(expression);
            }
            else if(expression.equals("natural"))
            {
                currentNotePitch.setAccidental(expression);
            }
        }
        else if(elements.peek().equals("duration"))
        {
                int duration = Integer.parseInt(expression);
            try {
                currentNoteRestValue = computeNoteRestValue(duration);
            } catch (UnsupportedNotationException e) {
                throw new SAXException(e);
            }

        }

    }

    //ErrorHandler


    public void error (SAXParseException e)
            throws SAXException
    {
        System.out.println("error");
        System.out.println("Public ID: "+e.getPublicId());
        System.out.println("System ID: "+e.getSystemId());
        System.out.println("Line number " + e.getLineNumber() + " Column number " + e.getColumnNumber());
        System.out.println("Message: " + e.getMessage());
        if(e.getMessage().contains("Document is invalid")){
            throw new SAXException(new IllegalDocumentTypeException());
        }
    }


    private NotePitch computeNotePitch()
    {
        int midiCode;
        if (currentNoteIndex <= 2) {
            midiCode = 2 * currentNoteIndex;
        } else {
            midiCode = 2 * currentNoteIndex - 1;
        }

        midiCode += NotePitch.SEMITONES_IN_SCALE * (currentNoteOctave + 1);

        return new NotePitch(midiCode);
    }


    private NoteRestValue computeNoteRestValue(int duration) throws UnsupportedNotationException {

        int value = duration*NoteRestValue.QUARTER.getRelativeValue()/divisions;
        NoteRestValue val = null;
        for(NoteRestValue v : NoteRestValue.values())
        {
            if(v.getRelativeValue() == value)
            {
                val = v; break;
            }
        }
        if(val == null) throw new UnsupportedNotationException();
        return val;
    }

}
