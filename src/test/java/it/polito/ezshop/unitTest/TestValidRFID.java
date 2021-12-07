package it.polito.ezshop.unitTest;

import org.junit.Test;

import static it.polito.ezshop.data.EZShop.validRFID;
import static org.junit.Assert.*;

public class TestValidRFID {

    @Test
    public void testNullRFID() {
        assertFalse(validRFID(null)); // NULL
    }

    @Test
    public void testRFIDLenght() {
        assertFalse(validRFID("")); // EMPTY STRING
        assertFalse(validRFID("00000010")); // Less than 12 digits
        assertFalse(validRFID("00000010000001")); //More than 12 digits
    }

    @Test
    public void testWellFormedRFID() {
        assertFalse(validRFID("abc000001000")); // Not well formed
    }

    @Test
    public void testPositiveRFID() {
        assertFalse(validRFID("000000000000")); // zero
        assertFalse(validRFID("-00000001000")); // negative
        assertTrue(validRFID("000000001000")); // valid
    }
}
