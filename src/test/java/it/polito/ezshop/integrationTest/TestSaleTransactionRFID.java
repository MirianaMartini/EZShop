package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.*;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.TreeMap;

public class TestSaleTransactionRFID {

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
    public void testSaleTransactionRFID(){
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

            /* if no user is logged */
            ezshop.logout();
            assertThrows(UnauthorizedException.class, ()->{ezshop.addProductToSaleRFID(saleId,
                    "000000000004");});

            ezshop.login("john", "5678");

            /* if no valid transaction ID */
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.addProductToSaleRFID(-1,
                    "000000000004");});

            /* if no valid rfid */
            assertThrows(InvalidRFIDException.class, ()->{ezshop.addProductToSaleRFID(saleId, null);});
            assertThrows(InvalidRFIDException.class, ()->{ezshop.addProductToSaleRFID(saleId, "");});
            assertThrows(InvalidRFIDException.class, ()->{ezshop.addProductToSaleRFID(saleId,
                    "123abc");});

            /* rfid does not exist */
            assertFalse(ezshop.addProductToSaleRFID(saleId, "000100000000"));

            /* add products */
            assertTrue(ezshop.addProductToSaleRFID(saleId, "000000000001"));
            assertTrue(ezshop.addProductToSaleRFID(saleId, "000000000004"));
            assertTrue(ezshop.addProductToSaleRFID(saleId, "000000000013"));

            SaleTransactionImpl saleTransaction = (SaleTransactionImpl) ezshop.getSaleTransactionInformations(saleId);
            assertSame(3, saleTransaction.getRFIDList().size());
            assertSame(2, saleTransaction.getEntries().size());

            /* if no user is logged */
            ezshop.logout();
            assertThrows(UnauthorizedException.class, ()->{ezshop.deleteProductFromSaleRFID(saleId,
                    "000000000004");});
            assertThrows(UnauthorizedException.class, ()->{ezshop.applyDiscountRateToProduct(saleId,
                    "8008234073496", 0.1);});
            assertThrows(UnauthorizedException.class, ()->{ezshop.applyDiscountRateToSale(saleId, 0.1);});
            assertThrows(UnauthorizedException.class, ()->{ezshop.computePointsForSale(saleId);});

            ezshop.login("john", "5678");

            /* if no valid transaction ID */
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.deleteProductFromSaleRFID(-1,
                    "000000000004");});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.applyDiscountRateToProduct(-1,
                    "8008234073496", 0.1);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.applyDiscountRateToSale(-1, 0.1);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.computePointsForSale(-1);});

            assertThrows(InvalidProductCodeException.class, ()->{ezshop.applyDiscountRateToProduct(saleId,
                    "800823496", 0.1);});

            /* if no valid rfid */
            assertThrows(InvalidRFIDException.class, ()->{ezshop.deleteProductFromSaleRFID(saleId, null);});
            assertThrows(InvalidRFIDException.class, ()->{ezshop.deleteProductFromSaleRFID(saleId, "");});
            assertThrows(InvalidRFIDException.class, ()->{ezshop.deleteProductFromSaleRFID(saleId,
                    "123abc");});

            /* rfid does not exist */
            assertFalse(ezshop.deleteProductFromSaleRFID(saleId, "000100000000"));

            /* delete one product */
            assertTrue(ezshop.deleteProductFromSaleRFID(saleId, "000000000013"));
            assertSame(2, saleTransaction.getRFIDList().size());
            assertSame(1, saleTransaction.getEntries().size());

            /* end sale transaction */
            assertTrue(ezshop.endSaleTransaction(saleId));

            /* delete sale transaction */
            assertTrue(ezshop.deleteSaleTransaction(saleId));


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

