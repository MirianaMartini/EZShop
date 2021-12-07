package it.polito.ezshop.unitTest;

import org.junit.Test;
import static it.polito.ezshop.data.EZShop.checkCustomerCard;
import static org.junit.Assert.*;

public class TestLoyaltyCardCode {

    @Test
    public void testNull(){
        assertFalse(checkCustomerCard(null));
    }

    @Test
    public void testEmpty(){
        assertFalse(checkCustomerCard(""));
    }

    @Test
    public void testShortMixed(){
        assertFalse(checkCustomerCard("1a12sc"));
    }

    @Test
    public void testLongMixed(){
        assertFalse(checkCustomerCard("123asd456asd"));
    }

    @Test
    public void testMixed(){
        assertFalse(checkCustomerCard("a123456789"));
    }

    @Test
    public void testCorrect(){
        assertTrue(checkCustomerCard("1234567890"));
    }

}
