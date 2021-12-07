package it.polito.ezshop.exceptions;

public class InvalidRFIDException extends Exception {
    public InvalidRFIDException() { super(); }
    public InvalidRFIDException(String msg) { super(msg); }
}
