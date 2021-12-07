package it.polito.ezshop.exceptions;

public class InvalidDiscountRateException extends Exception {
    public InvalidDiscountRateException() { super(); }
    public InvalidDiscountRateException(String msg) { super(msg); }
}
