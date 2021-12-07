package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestOrder {

    @Test
    public void testOrder() {
        Order o = new OrderImpl(17, "8005235078697", 7.50, 500);
        assertNotNull(o);

        int orderId = o.getOrderId();
        assertEquals(17, orderId);

        String productCode = o.getProductCode();
        assertEquals("8005235078697", productCode);

        double pricePerUnit = o.getPricePerUnit();
        assertEquals(7.50, pricePerUnit, 0.00001);

        int quantity = o.getQuantity();
        assertEquals(500, quantity);

        String status = o.getStatus();
        assertEquals("ISSUED", status);

        int newOrderId = 18;
        o.setOrderId(newOrderId);
        assertSame(18, o.getOrderId());

        String newProductCode = "8026912833279";
        o.setProductCode(newProductCode);
        assertEquals("8026912833279", o.getProductCode());

        String newStatus = "PAYED";
        o.setStatus(newStatus);
        assertEquals("PAYED", o.getStatus());

        double newPrice = 50.0;
        o.setPricePerUnit(newPrice);
        assertEquals(50.0, o.getPricePerUnit(), 0.00001);

        int newQuantity = 300;
        o.setQuantity(newQuantity);
        assertEquals(300, o.getQuantity());

        Integer balanceId = 42;
        o.setBalanceId(balanceId);
        assertSame(42, o.getBalanceId());
    }
}
