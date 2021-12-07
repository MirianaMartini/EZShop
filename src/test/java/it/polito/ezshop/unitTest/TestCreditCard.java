package it.polito.ezshop.unitTest;

import org.junit.Test;

import java.io.IOException;

import static it.polito.ezshop.data.EZShop.*;
import static it.polito.ezshop.data.EZShop.checkLuhn;
import static org.junit.Assert.*;

public class TestCreditCard {

    @Test
    public void testNullLuhn() {
        assertFalse(checkLuhn(null)); // NULL
    }

    @Test
    public void testLuhnLength() {
        assertFalse(checkLuhn("")); // EMPTY STRING
        assertFalse(checkLuhn("49927398716")); // Less than 16 digit
        assertFalse(checkLuhn("374652346956782346957823694857692364857368475368"));  // More than 16 digits
    }


    @Test
    public void testWellFormedLuhn() {
        assertFalse(checkLuhn("123456g812y45670")); // Not well formed
    }

    @Test
    public void testValidLuhn() {
        assertFalse(checkLuhn("1234567812345678")); // Not valid
        assertTrue(checkLuhn("1234567812345670")); // valid
    }


    @Test
    public void testNullGetCreditCard() {
        try {
            assertTrue(getCreditCards(null)[0].isEmpty()); // NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testEmptyGetCreditCard() {
        try {
            assertTrue(getCreditCards("")[0].isEmpty()); // EMPTY STRING
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreditCardNotFound() {
        try{
            assertTrue(getCreditCards("1234567812345670")[0].isEmpty());//Valid but not presents in the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreditCardFound() {
        try{
            assertEquals("5100293991053009", getCreditCards("5100293991053009")[0]); // Valid and presents in the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//@Test
    /*public void testUpdateCreditCardBalance() {
        // NO SCRITTURA FILE
        try {
            assertFalse(updateCreditCardBalance(null, 15.00, 5.00)); // NULL string
            assertFalse(updateCreditCardBalance("", 5.00, 15.00)); // EMPTY string
            assertFalse(updateCreditCardBalance("49927398716", 5.00, 15.00)); // Less than 16 digits
            assertFalse(updateCreditCardBalance("374652346956782346957823694857692364857368475368", 5.00, 1.00)); // More than 16 digits
            assertFalse(updateCreditCardBalance("8a0245312F023456", 3.00, 80.00));// Not well formed string
            assertFalse(updateCreditCardBalance("1234567812345678", 4.00, 6.00));// Not valid string
            assertFalse(updateCreditCardBalance("5100293991053009", 11.00, 45.00)); // Valid string in file but not oldbalance
            assertFalse(updateCreditCardBalance("5100293991053009", 10.00, 10.00)); // Valid string and oldbalance in file but the newbalance is the same of the oldbalance
            assertTrue(updateCreditCardBalance("5100293991053009", 10.00, 35.00)); // Valid string and oldbalance in file and the newbalance is differents from oldbalance
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
