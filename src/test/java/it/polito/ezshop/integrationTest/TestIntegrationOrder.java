package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

// TODO: test throws SQLException

public class TestIntegrationOrder {

    @BeforeClass
    public static void setEZShop(){
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

            /* one user can do orders and one not */
            User shopManager = new UserImpl(1, "emma", "1234", "ShopManager");
            User cashier = new UserImpl(2, "john", "5678", "Cashier");

            /* product to order */
            ProductType product = new ProductTypeImpl(1, "Pencil",
                    "8008234073496", 0.50, "Writes on paper");

            /* balance needed to pay orders */
            BalanceOperation balanceOperation = new BalanceOperationImpl(1, LocalDate.now(),
                    300.0, "CREDIT");

            db.insertUser(shopManager);
            db.insertUser(cashier);
            db.insertProductTypeDB((ProductTypeImpl) product);
            db.insertBalanceOperation(balanceOperation);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testIssueOrderThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* UnauthorizedException is thrown if no user is logged or not authorized to order */
            assertThrows(UnauthorizedException.class, ()->{ezshop.issueOrder("8008234073496",
                    100, 3.00);});
            assertThrows(UnauthorizedException.class, ezshop::getAllOrders);

            ezshop.login("john", "5678");
            assertThrows(UnauthorizedException.class, ()->{ezshop.issueOrder("8008234073496",
                    100, 3.00);});

            /* authorized user */
            ezshop.login("emma", "1234");

            /* InvalidProductCodeException is thrown if barcode is null, empty or invalid */
            assertThrows(InvalidProductCodeException.class, ()->{ezshop.issueOrder("123",
                    100, 3.00);});
            assertThrows(InvalidProductCodeException.class, ()->{ezshop.issueOrder(null,
                    100, 3.00);});
            assertThrows(InvalidProductCodeException.class, ()->{ezshop.issueOrder("",
                    100, 3.00);});

            /* InvalidQuantityException is thrown if quantity is equal or less than 0 */
            assertThrows(InvalidQuantityException.class, ()->{ezshop.issueOrder(
                    "8008234073496", 0, 3.00);});
            assertThrows(InvalidQuantityException.class, ()->{ezshop.issueOrder(
                    "8008234073496", -1, 3.00);});

            /* InvalidPricePerUnitException is thrown if selling price is equal or less than 0.0 */
            assertThrows(InvalidPricePerUnitException.class, ()->{ezshop.issueOrder("8008234073496",
                    100, 0.0);});
            assertThrows(InvalidPricePerUnitException.class, ()->{ezshop.issueOrder("8008234073496",
                    100, -3.00);});

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testIssueOrderFails() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            ezshop.login("emma", "1234");
            assertSame(-1, ezshop.issueOrder("8000036023242", 10, 1.0));

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidQuantityException | UnauthorizedException | InvalidPricePerUnitException | InvalidProductCodeException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testIssueOrderValidProduct() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            ezshop.login("emma", "1234");
            Integer id = ezshop.issueOrder("8008234073496", 10, 1.0);

            /* order is inserted in the system */
            List<Order> orders = ezshop.getAllOrders();

            Order o = orders.stream()
                    .filter(order -> order.getOrderId().equals(id))
                    .findAny()
                    .orElse(null);
            assertNotNull(o);
            assertEquals(id, o.getOrderId());
            assertEquals("8008234073496", o.getProductCode());
            assertEquals(10, o.getQuantity());
            assertEquals(1.0, o.getPricePerUnit(), 0.00001);
            assertEquals("ISSUED", o.getStatus());

            /* order is persistent in DB */
            TreeMap<Integer, Order> ordersDB = db.getOrders();
            assertTrue(ordersDB.containsKey(id));

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidQuantityException | UnauthorizedException | InvalidPricePerUnitException | InvalidProductCodeException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testPayOrderForThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* no user logged */
            assertThrows(UnauthorizedException.class, ()->{ezshop.payOrderFor("8008234073496",
                    100, 3.00);});

            /* unauthorized user */
            ezshop.login("john", "5678");
            assertThrows(UnauthorizedException.class, ()->{ezshop.payOrderFor("8008234073496",
                    100, 3.00);});

            /* authorized user */
            ezshop.login("emma", "1234");

            assertThrows(InvalidProductCodeException.class, ()->{ezshop.payOrderFor("123",
                    100, 2.00);});
            assertThrows(InvalidProductCodeException.class, ()->{ezshop.payOrderFor(null,
                    100, 2.00);});
            assertThrows(InvalidProductCodeException.class, ()->{ezshop.payOrderFor("",
                    100, 2.00);});

            assertThrows(InvalidQuantityException.class, ()->{ezshop.payOrderFor("8008234073496",
                    0, 3.00);});
            assertThrows(InvalidQuantityException.class, ()->{ezshop.payOrderFor("8008234073496",
                    -1, 3.00);});

            assertThrows(InvalidPricePerUnitException.class, ()->{ezshop.payOrderFor("8008234073496",
                    100, 0.0);});
            assertThrows(InvalidPricePerUnitException.class, ()->{ezshop.payOrderFor("8008234073496",
                    100, -3.00);});

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        } catch (InvalidPasswordException | InvalidUsernameException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPayOrderForFails() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            ezshop.login("emma", "1234");

            /* non existent product */
            assertSame(-1, ezshop.payOrderFor("8000036023242",
                    10, 1.0));

            /* balance not sufficient for 600 */
            assertSame(-1, ezshop.payOrderFor("8008234073496",
                    200, 3.00));

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidQuantityException | UnauthorizedException | InvalidPricePerUnitException | InvalidProductCodeException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testPayOrderForValid() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            ezshop.login("emma", "1234");

            /* balance before order */
            double balance = ezshop.computeBalance();
            /* order needs 100 */
            Integer id = ezshop.payOrderFor("8008234073496", 100, 1.0);

            List<Order> orders = ezshop.getAllOrders();

            List<BalanceOperation> balanceOperations = ezshop.getCreditsAndDebits(LocalDate.MIN, LocalDate.MAX);

            Order o = orders.stream()
                    .filter(order -> order.getOrderId().equals(id))
                    .findAny()
                    .orElse(null);
            assertNotNull(o);
            assertEquals("8008234073496", o.getProductCode());
            assertEquals(100, o.getQuantity());
            assertEquals(1.0, o.getPricePerUnit(), 0.00001);
            assertEquals("PAYED", o.getStatus());

            BalanceOperation b = balanceOperations.stream()
                    .filter(bo -> bo.getBalanceId() == (o.getBalanceId()))
                    .findAny()
                    .orElse(null);
            assertNotNull(b);

            TreeMap<Integer, Order> ordersDB = db.getOrders();
            assertTrue(ordersDB.containsKey(id));

            double newBalance = ezshop.computeBalance();
            assertEquals(balance-newBalance, o.getQuantity()*o.getPricePerUnit(),
                    0.00001);

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidQuantityException | UnauthorizedException | InvalidPricePerUnitException | InvalidProductCodeException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testPayOrderThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            assertThrows(UnauthorizedException.class, ()->{ezshop.payOrder(1);});

            ezshop.login("john", "5678");
            assertThrows(UnauthorizedException.class, ()->{ezshop.payOrder(1);});

            ezshop.login("emma", "1234");

            /* Throws InvalidOrderIdException if order id is null or less or equal to 0 */
            assertThrows(InvalidOrderIdException.class, ()->{ezshop.payOrder(null);});
            assertThrows(InvalidOrderIdException.class, ()->{ezshop.payOrder(0);});
            assertThrows(InvalidOrderIdException.class, ()->{ezshop.payOrder(-1);});

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        } catch (InvalidPasswordException | InvalidUsernameException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPayOrderFails() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            ezshop.login("emma", "1234");

            /* pay an issued order with not enough balance (600) */
            Integer idTooMuch = ezshop.issueOrder("8008234073496", 200, 3.00);
            assertFalse(ezshop.payOrder(idTooMuch));

            /* pay a not existing order */
            assertFalse(ezshop.payOrder(100));

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | UnauthorizedException | InvalidOrderIdException | InvalidQuantityException | InvalidPricePerUnitException | InvalidProductCodeException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testPayOrderValid() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            ezshop.login("emma", "1234");
            double balance = ezshop.computeBalance();

            /* issue an order to pay 100 */
            int toOrder = ezshop.issueOrder("8008234073496", 100, 1.00);
            assertTrue(ezshop.payOrder(toOrder));

            List<Order> orders = ezshop.getAllOrders();

            List<BalanceOperation> balanceOperations = ezshop.getCreditsAndDebits(LocalDate.MIN, LocalDate.MAX);

            Order o = orders.stream()
                    .filter(order -> order.getOrderId().equals(toOrder))
                    .findAny()
                    .orElse(null);
            assertNotNull(o);
            assertEquals("PAYED", o.getStatus());

            BalanceOperation b = balanceOperations.stream()
                    .filter(bo -> bo.getBalanceId() == (o.getBalanceId()))
                    .findAny()
                    .orElse(null);
            assertNotNull(b);

            TreeMap<Integer, Order> ordersDB = db.getOrders();
            assertEquals("PAYED", ordersDB.get(toOrder).getStatus());
            assertSame(b.getBalanceId(), ordersDB.get(toOrder).getBalanceId());

            double newBalance = ezshop.computeBalance();
            assertEquals(balance-newBalance, o.getQuantity()*o.getPricePerUnit(),
                    0.00001);

            // Call payOrder on a PAYED order
            assertTrue(ezshop.payOrder(toOrder));
            assertEquals(newBalance, ezshop.computeBalance(), 0.00001);

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | UnauthorizedException | InvalidOrderIdException | InvalidQuantityException | InvalidProductCodeException | InvalidPricePerUnitException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testRecordOrderArrival() {
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* no logged user */
            assertThrows(UnauthorizedException.class, ()->{ezshop.recordOrderArrival(1);});

            /* unauthorized user */
            ezshop.login("john", "5678");
            assertThrows(UnauthorizedException.class, ()->{ezshop.recordOrderArrival(1);});

            /* authorized user */
            ezshop.login("emma", "1234");

            assertThrows(InvalidOrderIdException.class, ()->{ezshop.recordOrderArrival(null);});
            assertThrows(InvalidOrderIdException.class, ()->{ezshop.recordOrderArrival(0);});
            assertThrows(InvalidOrderIdException.class, ()->{ezshop.recordOrderArrival(-1);});

            /* record an not existent order */
            assertFalse(ezshop.recordOrderArrival(100));

            /* issue an order for a product with no specified location */
            int id = ezshop.issueOrder("8008234073496", 100, 1.0);

            /* record an issued order returns false */
            assertFalse(ezshop.recordOrderArrival(id));

            /* pay the order still no specified location */
            ezshop.payOrder(id);
            assertThrows(InvalidLocationException.class, ()->{ezshop.recordOrderArrival(id);});

            /* update product location and set quantity to 0 */
            ProductType p = ezshop.getProductTypeByBarCode("8008234073496");
            ezshop.updatePosition(p.getId(), "12-A-12");
            int quantity = 0;
            ezshop.updateQuantity(p.getId(), quantity);

            assertTrue(ezshop.recordOrderArrival(id));

            /* order is stored in the system */
            List<Order> orders = ezshop.getAllOrders();

            Order o = orders.stream()
                    .filter(order -> order.getOrderId().equals(id))
                    .findAny()
                    .orElse(null);
            assertNotNull(o);
            assertEquals("COMPLETED", o.getStatus());

            /* quantity of product is updated */
            int newQuantity = p.getQuantity();
            assertSame(o.getQuantity(), newQuantity-quantity);

            /* order is updated in DB */
            TreeMap<Integer, Order> ordersDB = db.getOrders();
            assertEquals("COMPLETED", ordersDB.get(id).getStatus());

            /* Call record order arrival on a Completed order has no effect */
            assertTrue(ezshop.recordOrderArrival(id));
            assertSame(newQuantity, p.getQuantity());

            /* Call pay order on a completed order returns false */
            assertFalse(ezshop.payOrder(id));

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidQuantityException | UnauthorizedException | InvalidPricePerUnitException | InvalidProductCodeException | InvalidOrderIdException | InvalidLocationException | InvalidProductIdException throwables) {
            fail(throwables.getMessage());
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
