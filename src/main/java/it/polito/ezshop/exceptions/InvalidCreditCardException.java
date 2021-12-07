package it.polito.ezshop.exceptions;

public class InvalidCreditCardException extends Exception {
    public InvalidCreditCardException() { super(); }
    public InvalidCreditCardException(String msg) { super(msg); }
}
