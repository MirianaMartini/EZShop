package it.polito.ezshop.exceptions;

public class InvalidProductIdException extends Exception {
    public InvalidProductIdException() { super(); }
    public InvalidProductIdException(String msg) { super(msg); }
}
