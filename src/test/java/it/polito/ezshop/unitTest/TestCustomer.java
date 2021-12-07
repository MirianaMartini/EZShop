package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.Customer;
import it.polito.ezshop.data.CustomerImpl;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCustomer {

    @Test
    public void testCustomer(){
        Customer c = new CustomerImpl(1, "customer");
        assertNotNull(c);

        Customer c2 = new CustomerImpl(2, "another customer", "0123456789", 10);
        assertNotNull(c2);

        int points = c.getPoints();
        assertEquals(0, points);

        String card = c.getCustomerCard();
        assertNull(card);

        int id = c.getId();
        assertEquals(1, id);

        String name = c.getCustomerName();
        assertEquals("customer", name);


        c.setCustomerCard("1234567890");
        String newCard = c.getCustomerCard();
        assertEquals("1234567890", newCard);

        c.setPoints(10);
        int newPoints = c.getPoints();
        assertEquals(10, newPoints);

        c.setCustomerName("new name");
        String newName = c.getCustomerName();
        assertEquals("new name", newName);

        c.setId(2);
        int newId = c.getId();
        assertEquals(2, newId);

        c.setCustomerCard(null);
        String cardNull = c.getCustomerCard();
        assertNull(cardNull);
    }

}
