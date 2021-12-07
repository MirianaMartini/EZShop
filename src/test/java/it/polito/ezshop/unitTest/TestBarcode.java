package it.polito.ezshop.unitTest;

import org.junit.Test;

import static it.polito.ezshop.data.EZShop.validBarcode;
import static org.junit.Assert.*;

public class TestBarcode {

    @Test
    public void testNullBarcode() {
        assertFalse(validBarcode(null)); // NULL
    }

    @Test
    public void testBarcodeLenght() {
        assertFalse(validBarcode("")); // EMPTY STRING
        assertFalse(validBarcode("40170725")); // Less than 12 digits
        assertFalse(validBarcode("990000100000001862"));  // More than 14 digits
    }

    @Test
    public void testWellFormedBarcode() {
        assertFalse(validBarcode("8a02453123024")); // Not well formed
    }

    @Test
    public void testValidBarcode() {
        assertFalse(validBarcode("8002453123023")); // Not valid
        assertTrue(validBarcode("8002453123024")); // Valid
    }
}
