package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTicketEntry {

    @Test
    public void testTicketEntryClass() {
        TicketEntry t = new TicketEntryImpl("8005235155961",
                "Notebook", 10, 1.50, 0.0);
        assertNotNull(t);

        String barcode = t.getBarCode();
        assertEquals("8005235155961", barcode);

        String productDescription = t.getProductDescription();
        assertEquals("Notebook", productDescription);

        int amount = t.getAmount();
        assertEquals(10, amount);

        double pricePerUnit = t.getPricePerUnit();
        assertEquals(1.50, pricePerUnit, 0.00001);

        double discountRate = t.getDiscountRate();
        assertEquals(0.0, discountRate, 0.00001);

        String newBarcode = "8026912833279";
        t.setBarCode(newBarcode);
        assertEquals("8026912833279", t.getBarCode());

        String newDescription = "Pencil";
        t.setProductDescription(newDescription);
        assertEquals("Pencil", t.getProductDescription());

        double newPrice = 0.50;
        t.setPricePerUnit(newPrice);
        assertEquals(0.50, t.getPricePerUnit(), 0.00001);

        int newAmount = 5;
        t.setAmount(newAmount);
        assertEquals(5, t.getAmount());

        double newDiscountRate = 0.3;
        t.setDiscountRate(newDiscountRate);
        assertEquals(0.3, t.getDiscountRate(), 0.00001);
    }
}
