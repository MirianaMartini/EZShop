package it.polito.ezshop.exceptions;

public class InvalidCustomerNameException extends Exception {
    public InvalidCustomerNameException() { super(); }
    public InvalidCustomerNameException(String msg) { super(msg); }
}
