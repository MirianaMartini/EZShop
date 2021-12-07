package it.polito.ezshop.exceptions;

public class InvalidProductDescriptionException extends Exception {
    public InvalidProductDescriptionException() { super(); }
    public InvalidProductDescriptionException(String msg) { super(msg); }
}
