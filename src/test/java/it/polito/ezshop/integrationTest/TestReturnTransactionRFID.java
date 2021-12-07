package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class TestReturnTransactionRFID {

    @Before
    public void setEZShop() {
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

            /* authorized user in DB */
            User shopManager = new UserImpl(1, "emma", "1234", "ShopManager");
            User cashier = new UserImpl(2, "john", "5678", "Cashier");
            db.insertUser(shopManager);
            db.insertUser(cashier);

            /* products in Sale Transactions */
            ProductType p1 = new ProductTypeImpl(1, "Pencil",
                    "8008234073496", 0.50, "Writes on paper");
            ProductType p2 = new ProductTypeImpl(2, "Notebook",
                    "8010333001874", 2.00, "With blank pages");

            p1.setLocation("10-A-10");
            p2.setLocation("10-B-10");
            db.insertProductTypeDB((ProductTypeImpl) p1);
            db.insertProductTypeDB((ProductTypeImpl) p2);
            db.updatePositionDB(p1.getId(), p1.getLocation());
            db.updatePositionDB(p2.getId(), p2.getLocation());

            BalanceOperation balanceOperation = new BalanceOperationImpl(1, LocalDate.now(),
                    300.0, "CREDIT");
            db.insertBalanceOperation(balanceOperation);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testReturnTransactionRFID(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            ezshop.login("emma", "1234");
            /* record RFID in system */
            int order1 = ezshop.issueOrder("8008234073496", 10, 0.20);
            int order2 = ezshop.issueOrder("8010333001874", 10, 1.00);

            ezshop.payOrder(order1);
            ezshop.payOrder(order2);

            assertTrue(ezshop.recordOrderArrivalRFID(order1, "000000000001"));
            assertTrue(ezshop.recordOrderArrivalRFID(order2, "000000000011"));

            ezshop.logout();
            ezshop.login("john", "5678");

            int saleId = ezshop.startSaleTransaction();

            /* add products */
            assertTrue(ezshop.addProductToSaleRFID(saleId, "000000000001"));
            assertTrue(ezshop.addProductToSaleRFID(saleId, "000000000004"));
            assertTrue(ezshop.addProductToSaleRFID(saleId, "000000000013"));

            /* end sale transaction */
            assertTrue(ezshop.endSaleTransaction(saleId));

            /* pay sale transaction */
            assertEquals(7.00, ezshop.receiveCashPayment(saleId, 10.0), 0.00001);

            /* start return transaction */
            int returnId = ezshop.startReturnTransaction(saleId);

            /* if no user is logged */
            ezshop.logout();

            assertThrows(UnauthorizedException.class, ()->{ezshop.returnProductRFID(returnId,
                    "000000000013");});

            ezshop.login("john", "5678");

            /* invalid return id */
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.returnProductRFID(-1,
                    "000000000013");});

            /* invalid RFID */
            assertThrows(InvalidRFIDException.class, ()->{ezshop.returnProductRFID(returnId,
                    "0000000013");});

            /* return product */
            assertTrue(ezshop.returnProductRFID(returnId, "000000000013"));

            /* end with commit false */
            assertTrue(ezshop.endReturnTransaction(returnId, false));

            /* start return transaction */
            int returnId2 = ezshop.startReturnTransaction(saleId);

            /* return product */
            assertTrue(ezshop.returnProductRFID(returnId2, "000000000013"));

            /* end with commit true */
            assertTrue(ezshop.endReturnTransaction(returnId2, true));

            /* delete return transaction */
            assertTrue(ezshop.deleteReturnTransaction(returnId2));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidOrderIdException e) {
            e.printStackTrace();
        } catch (InvalidRFIDException e) {
            e.printStackTrace();
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
        }

    }

    @After
    public void resetEZShop() {
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }
}
