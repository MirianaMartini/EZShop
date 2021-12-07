package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TestIntegrationBalanceOperation {

    @BeforeClass
    public static void setEZShop() {

        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

            /* one user can call methods and one not */
            User shopManager = new UserImpl(1, "emma", "1234", "ShopManager");
            User cashier = new UserImpl(2, "john", "5678", "Cashier");

            db.insertUser(shopManager);
            db.insertUser(cashier);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testRecordBalanceUpdateThrowsException(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();
            /* UnauthorizedException is thrown if no user is logged or not authorized */
            assertThrows(UnauthorizedException.class, ()->{ezshop.recordBalanceUpdate(100.0);});

            ezshop.login("john", "5678");
            assertThrows(UnauthorizedException.class, ()->{ezshop.recordBalanceUpdate(100.0);});

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testRecordBalanceUpdateFails(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("emma", "1234");

            /* balance is 0 so no DEBIT can be recorded */
            assertFalse(ezshop.recordBalanceUpdate(-100.0));

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testRecordBalanceUpdate(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("emma", "1234");

            /* CREDIT of 100 can be recorded */
            assertTrue(ezshop.recordBalanceUpdate(100.0));
            /* DEBIT of 10 can be recorded */
            assertTrue(ezshop.recordBalanceUpdate(-10.0));

            /* BalanceOperations stored in the system */
            List<BalanceOperation> balanceOperations = ezshop.getCreditsAndDebits(null, null);
            assertEquals(2, balanceOperations.size());

            /* one limit null */
            assertEquals(2, ezshop.getCreditsAndDebits(LocalDate.MIN, null).size());
            assertEquals(2, ezshop.getCreditsAndDebits(null, LocalDate.MAX).size());

            /* from and to inverted */
            assertEquals(2, ezshop.getCreditsAndDebits(LocalDate.MAX, LocalDate.MIN).size());

            BalanceOperation c = balanceOperations.stream().
                    filter(b -> b.getType().equals("CREDIT"))
                    .findAny()
                    .orElse(null);
            assertNotNull(c);
            assertEquals(100.0, c.getMoney(), 0.00001);

            BalanceOperation d = balanceOperations.stream().
                    filter(b -> b.getType().equals("DEBIT"))
                    .findAny()
                    .orElse(null);
            assertNotNull(d);
            assertEquals(10.0, d.getMoney(), 0.00001);

            /* balance operations persistent in DB */
            TreeMap<Integer, BalanceOperation> balanceOperationsDB = db.getBalanceOperations();
            assertEquals(2, balanceOperationsDB.size());

            /* total balance computed */
            assertEquals(c.getMoney()-d.getMoney(), ezshop.computeBalance(), 0.00001);

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testComputeBalanceThrowsException(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();
            /* UnauthorizedException is thrown if no user is logged or not authorized */
            assertThrows(UnauthorizedException.class, ezshop::computeBalance);

            ezshop.login("john", "5678");
            assertThrows(UnauthorizedException.class, ezshop::computeBalance);

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException throwables) {
            throwables.printStackTrace();
        }
    }

    @AfterClass
    public static void resetEZShop(){
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }
}
