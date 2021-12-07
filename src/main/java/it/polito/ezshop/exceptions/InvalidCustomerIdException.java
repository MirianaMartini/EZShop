package it.polito.ezshop.exceptions;

public class InvalidCustomerIdException extends Exception{
    public InvalidCustomerIdException() { super(); }
    public InvalidCustomerIdException(String msg) { super(msg); }
}
