package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.Product;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestProduct {

    @Test
    public void testProduct(){
        Product p1 = new Product("0000001000", "8000825830419");
        assertNotNull(p1);

        Product p2 = new Product("0000001001", "8000825830419", 10);
        assertNotNull(p2);


        String rfid = p1.getRfid();
        assertEquals("0000001000", rfid);

        String barcode = p1.getBarcode();
        assertEquals("8000825830419", barcode);

        int saleId = p1.getSaleId();
        assertEquals(-1, saleId);


        p1.setRfid("0000001111");
        String newRFID = p1.getRfid();
        assertEquals("0000001111", newRFID);

        p1.setBarcode("8000825830420");
        String newBarcode = p1.getBarcode();
        assertEquals("8000825830420", newBarcode);

        p1.setSaleId(10);
        int newSaleId = p1.getSaleId();
        assertEquals(10, newSaleId);
    }

}
