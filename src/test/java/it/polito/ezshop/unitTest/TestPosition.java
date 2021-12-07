package it.polito.ezshop.unitTest;

import org.junit.Test;

import static it.polito.ezshop.data.EZShop.validPosition;
import static org.junit.Assert.*;

public class TestPosition {

    @Test
    public void testPositionNumberOfFields() {
        assertFalse(validPosition("12-A")); // fields != 3
    }

    @Test
    public void testPositionSeparator() {
        assertFalse(validPosition("12-A,18")); // incorrect separator
    }

    @Test
    public void testPositionFieldType() {
        assertFalse(validPosition("G-A-18")); // aisle not number
        assertFalse(validPosition("12-4-18"));  // rack not a letter
        assertFalse(validPosition("12-A-dieci")); // level not a number
    }

    @Test
    public void testPositionFieldSign() {
        assertFalse(validPosition("-12-A-18")); // aisle =< 0
        assertFalse(validPosition("12-A-0")); // level =< 0
    }

    @Test
    public void testPositionWellFormed(){
        assertFalse(validPosition("0012-A-18")); // aisle not well formed
        assertFalse(validPosition("12-Aaa-18")); // rack not well formed
        assertFalse(validPosition("12-A-00018")); // level not well formed
    }

    @Test
    public void testValidPosition() {
        assertTrue(validPosition("12-A-18")); // valid
    }
}
