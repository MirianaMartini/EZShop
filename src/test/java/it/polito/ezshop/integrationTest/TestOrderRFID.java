package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.TreeMap;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class TestOrderRFID {

    @Before
    public void setEZShop() {
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

            /* one user can do orders and one not */
            User shopManager = new UserImpl(1, "emma", "1234", "ShopManager");
            User cashier = new UserImpl(2, "john", "5678", "Cashier");
            db.insertUser(shopManager);
            db.insertUser(cashier);

            /* products in Sale Transactions */
            ProductType p1 = new ProductTypeImpl(1, "Pencil",
                    "8008234073496", 0.50, "Writes on paper");
            ProductType p2 = new ProductTypeImpl(2, "Notebook",
                    "8010333001874", 2.00, "With blank pages");

            /* only one product with location */
            p1.setLocation("10-A-10");
            db.insertProductTypeDB((ProductTypeImpl) p1);
            db.updatePositionDB(p1.getId(), p1.getLocation());
            db.insertProductTypeDB((ProductTypeImpl) p2);

            /* balance needed to pay orders */
            BalanceOperation balanceOperation = new BalanceOperationImpl(1, LocalDate.now(),
                    300.0, "CREDIT");
            db.insertBalanceOperation(balanceOperation);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testOrderRFID(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("emma", "1234");

            /* order and pay 10 units of p2 */
            int id = ezshop.issueOrder("8010333001874", 10, 1.00);

            /* returns false if on issued order */
            assertFalse(ezshop.recordOrderArrivalRFID(id, "000000000001"));
            ezshop.payOrder(id);

            ezshop.logout();

            /* if no user is logged or if user has no rights throws UnauthorizedException */
            assertThrows(UnauthorizedException.class, ()->{ezshop.recordOrderArrivalRFID(id,
                    "000000000001");});

            ezshop.login("john", "5678");
            assertThrows(UnauthorizedException.class, ()->{ezshop.recordOrderArrivalRFID(id,
                    "000000000001");});

            /* authorized user */
            ezshop.login("emma", "1234");

            /* if no valid order id */
            assertThrows(InvalidOrderIdException.class, ()->{ezshop.recordOrderArrivalRFID(null,
                    "000000000001");});
            assertThrows(InvalidOrderIdException.class, ()->{ezshop.recordOrderArrivalRFID(0,
                    "000000000001");});
            assertThrows(InvalidOrderIdException.class, ()->{ezshop.recordOrderArrivalRFID(-1,
                    "000000000001");});

            /* if no valid rfid */
            assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id,
                    null);});
            assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id,
                    "");});
            assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id,
                    "123abc");});

            /* record an not existent order */
            assertFalse(ezshop.recordOrderArrivalRFID(100, "000000000001"));

            /* record with no specified location */
            assertThrows(InvalidLocationException.class, ()->{ezshop.recordOrderArrivalRFID(id, "000000000001");});

            /* issue and pay order for a product with specified location */
            int order = ezshop.issueOrder("8008234073496", 10, 0.20);
            ezshop.payOrder(order);

            assertTrue(ezshop.recordOrderArrivalRFID(order, "000000000001"));

            TreeMap<String, Product> products = db.getAllProduct();
            assertSame(10, products.size());

            /* issue and pay another order for p1 */
            int newOrder = ezshop.issueOrder("8008234073496", 10, 0.20);
            ezshop.payOrder(newOrder);

            /* if rfid already exists */
            assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(newOrder,
                    "000000000001");});
            assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(newOrder,
                    "000000000004");});

        } catch (InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidQuantityException | InvalidPricePerUnitException | InvalidProductCodeException | InvalidOrderIdException throwables) {
            fail(throwables.getMessage());
        } catch (InvalidRFIDException | InvalidLocationException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
