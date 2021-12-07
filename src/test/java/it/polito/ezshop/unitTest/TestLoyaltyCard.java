package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.LoyaltyCardImpl;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestLoyaltyCard {

    @Test
    public void testLoyaltyCard(){
        LoyaltyCardImpl lc = new LoyaltyCardImpl("1234567890", false);

        String code = lc.getCode();
        assertEquals("1234567890", code);

        boolean assigned = lc.isAssigned();
        assertFalse(assigned);
    }
}
