package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestSaleTransaction {

    @Test
    public void testSaleTransaction() {
        SaleTransactionImpl st = new SaleTransactionImpl(1);
        assertNotNull(st);

        //GET & SET: id
        int saleId=st.getTicketNumber();
        assertEquals(1,saleId);

        st.setTicketNumber(2);
        int newId=st.getTicketNumber();
        assertEquals(2, newId);

        //GET & SET: Price
        double price = st.getPrice();
        assertEquals(0.0, price, 0.00001);

        st.setPrice(10.50);
        assertEquals(10.50, st.getPrice(), 0.00001);

        //GET & SET: State
        String state = st.getState();
        assertEquals("OPEN", state);

        st.setState("CLOSED");
        assertEquals("CLOSED", st.getState());

        //GET & SET: BalanceID
        st.setBalanceId(90);
        int newBalance=st.getBalanceId();
        assertEquals(90, newBalance);

        //GET & SET: DiscountRate
        double discount=st.getDiscountRate();
        assertEquals(0.0, discount, 0.00001);

        st.setDiscountRate(25.00);
        double newDiscount=st.getDiscountRate();
        assertEquals(25.00, newDiscount, 0.00001);

        //GET & SET: EntryList
        List<TicketEntry> entryList = st.getEntries();
        assertNotNull(st.getEntries());
        assertTrue(entryList.isEmpty());

        TicketEntry t1 = new TicketEntryImpl("8005235155961",
                "Notebook", 10, 1.50, 0.0);
        TicketEntry t2 = new TicketEntryImpl("8008234073496",
                "Glue", 5, 2.00, 0.0);


        List<TicketEntry> newEntryList = new ArrayList<>();
        newEntryList.add(t1);
        newEntryList.add(t2);
        st.setEntries(newEntryList);
        assertEquals(2, st.getEntries().size());
        assertEquals(newEntryList, st.getEntries());
        assertTrue(st.getEntries().contains(t1));
        assertTrue(st.getEntries().contains(t2));


        //GET & SET: RFIDList
        List<String> RFIDList = st.getRFIDList();
        assertNotNull(st.getRFIDList());
        assertTrue(RFIDList.isEmpty());

        Product p1= new Product("0000000006", "8005235155961");
        Product p2= new Product("0000000004", "8008234073496");

        List<String> newRFIDList = new ArrayList<>();
        newRFIDList.add(p1.getRfid());
        newRFIDList.add(p2.getRfid());
        st.setRFIDList(newRFIDList);
        assertEquals(2, st.getRFIDList().size());
        assertEquals(newRFIDList, st.getRFIDList());
        assertTrue(st.getEntries().contains(t1));
        assertTrue(st.getEntries().contains(t2));

    }
}
