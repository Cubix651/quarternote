package com.note.quarter;

public class IllegalDocumentTypeException extends Exception {

    public IllegalDocumentTypeException(){}
    public IllegalDocumentTypeException(String s){super(s);}
    public IllegalDocumentTypeException(String s, Throwable t){super(s,t);}
    public IllegalDocumentTypeException(Throwable t){super(t);}

}
