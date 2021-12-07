package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.*;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TestBalanceOperation {

    @Test
    public void testBalanceOperation() {
        BalanceOperation b = new BalanceOperationImpl(17,
                LocalDate.of(2021, 5, 19), 100, "DEBIT");
        assertNotNull(b);

        int balanceId = b.getBalanceId();
        assertEquals(17, balanceId);

        LocalDate date = b.getDate();
        assertEquals( LocalDate.of(2021, 5, 19), date);

        double money = b.getMoney();
        assertEquals(100, money, 0.00001);

        String type = b.getType();
        assertEquals("DEBIT", type);

        int newBalanceId = 18;
        b.setBalanceId(newBalanceId);
        assertEquals(18, b.getBalanceId());

        LocalDate newDate = LocalDate.of(2021, 5, 20);
        b.setDate(newDate);
        assertEquals(LocalDate.of(2021, 5, 20), b.getDate());

        double newMoney = 150;
        b.setMoney(newMoney);
        assertEquals(150, newMoney, 0.00001);

        String newType = "CREDIT";
        b.setType(newType);
        assertEquals("CREDIT", b.getType());
    }
}
