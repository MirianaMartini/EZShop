package it.polito.ezshop.exceptions;

public class InvalidPricePerUnitException extends Exception {
    public InvalidPricePerUnitException() { super(); }
    public InvalidPricePerUnitException(String msg) { super(msg); }
}
