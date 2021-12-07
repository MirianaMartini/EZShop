package it.polito.ezshop.exceptions;

public class UnauthorizedException extends Exception {
    public UnauthorizedException() { super(); }
    public UnauthorizedException(String msg) { super(msg); }
}
