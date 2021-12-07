package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestReturnTransaction {

    @Test
    public void testReturnTransaction() {
        ReturnTransaction rt = new ReturnTransaction(1, 2);
        assertNotNull(rt);

        int returnId = rt.getId();
        assertEquals(1, returnId);

        int saleId = rt.getSaleTransactionId();
        assertEquals(2, saleId);

        double price = rt.getPrice();
        assertEquals(0.0, price, 0.00001);

        String state = rt.getState();
        assertEquals("OPEN", state);

        List<TicketEntry> entryList = rt.getEntryList();
        assertNotNull(rt.getEntryList());
        assertTrue(entryList.isEmpty());

        int newReturnId = 10;
        rt.setId(newReturnId);
        assertSame(10, rt.getId());

        int newSaleId = 10;
        rt.setSaleTransactionId(newSaleId);
        assertSame(10, rt.getSaleTransactionId());

        TicketEntry t1 = new TicketEntryImpl("8005235155961",
                "Notebook", 10, 1.50, 0.0);
        TicketEntry t2 = new TicketEntryImpl("8008234073496",
                "Glue", 5, 2.00, 0.0);

        // TODO: check if correct
        List<TicketEntry> newEntryList = new ArrayList<>();
        newEntryList.add(t1);
        newEntryList.add(t2);
        rt.setEntryList(newEntryList);
        assertEquals(2, rt.getEntryList().size());
        assertEquals(newEntryList, rt.getEntryList());
        assertTrue(rt.getEntryList().contains(t1));
        assertTrue(rt.getEntryList().contains(t2));

        double newPrice = 3.50;
        rt.setPrice(newPrice);
        assertEquals(3.50, rt.getPrice(), 0.00001);

        int balanceId = 33;
        rt.setBalanceId(balanceId);
        assertSame(33, rt.getBalanceId());

        String newState = "PAYED";
        rt.setState(newState);
        assertEquals("PAYED", rt.getState());


        //GET & SET: RFIDList
        List<String> RFIDList = rt.getRFIDList();
        assertNotNull(rt.getRFIDList());
        assertTrue(RFIDList.isEmpty());

        Product p1= new Product("0000000006", "8005235155961");
        Product p2= new Product("0000000004", "8008234073496");

        List<String> newRFIDList = new ArrayList<>();
        newRFIDList.add(p1.getRfid());
        newRFIDList.add(p2.getRfid());
        rt.setRFIDList(newRFIDList);
        assertEquals(2, rt.getRFIDList().size());
        assertEquals(newRFIDList, rt.getRFIDList());
    }
}
