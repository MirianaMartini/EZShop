package it.polito.ezshop.exceptions;

public class InvalidUsernameException extends Exception {
    public InvalidUsernameException() { super(); }
    public InvalidUsernameException(String msg) { super(msg); }
}
