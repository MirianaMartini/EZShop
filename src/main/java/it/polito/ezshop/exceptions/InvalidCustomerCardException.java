package it.polito.ezshop.exceptions;

public class InvalidCustomerCardException extends Exception {
    public InvalidCustomerCardException() { super(); }
    public InvalidCustomerCardException(String msg) { super(msg); }
}
