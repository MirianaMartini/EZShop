package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestIntegrationReturnTransaction {

    @Before
    public void setEZShop(){
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

            /* authorized user in DB */
            User cashier = new UserImpl(2, "john", "5678", "Cashier");
            db.insertUser(cashier);

            /* products in Sale Transactions */
            ProductType p1 = new ProductTypeImpl(1, "Pencil",
                    "8008234073496", 0.50, "Writes on paper");
            ProductType p2 = new ProductTypeImpl(2, "Notebook",
                    "8010333001874", 2.00, "With blank pages");
            /* Product not in Sale Transaction */
            ProductType p3 = new ProductTypeImpl(3, "Rubber",
                    "8026912833279", 1.50, "To erase things");

            p1.setLocation("10-A-10");
            p2.setLocation("10-B-10");
            p1.setQuantity(100);
            p2.setQuantity(100);
            db.insertProductTypeDB((ProductTypeImpl) p1);
            db.insertProductTypeDB((ProductTypeImpl) p2);
            db.insertProductTypeDB((ProductTypeImpl) p3);
            db.updateQuantityDB(p1.getId(), p1.getQuantity());
            db.updateQuantityDB(p2.getId(), p2.getQuantity());

            /* Two sale transactions for test */
            SaleTransactionImpl st1 = new SaleTransactionImpl(1);
            SaleTransactionImpl st2 = new SaleTransactionImpl(2);

            /* 10 pencils for 5 euros */
            TicketEntry t1 = new TicketEntryImpl("8008234073496", "Pencil", 10,
                    0.50, 0.0);
            db.insertTicketEntry(1, t1);

            /* 2 notebooks with discount rate 50% for 2 euros */
            TicketEntry t2 = new TicketEntryImpl("8010333001874", "Notebook", 2,
                    2.00, 0.5);
            db.insertTicketEntry(1, t2);

            st1.setPrice(7.00);

            /* Credit of 7 euros for sale transaction */
            BalanceOperation b = new BalanceOperationImpl(1, LocalDate.now(), 7.0, "CREDIT");
            db.insertBalanceOperation(b);

            st1.setBalanceId(1);
            st1.setState("PAYED");
            db.insertSaleTransaction(st1);
            db.insertSaleTransaction(st2);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testStartReturnTransactionThrowsException(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* Throws UnauthorizedException if no user logged */
            assertThrows(UnauthorizedException.class, ()->{ezshop.startReturnTransaction(1);});

            /* authorized user */
            ezshop.login("john", "5678");

            /* Throws InvalidTransactionIdException if transactionId is null or less or equal to 0 */
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.startReturnTransaction(null);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.startReturnTransaction(0);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.startReturnTransaction(-1);});


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testStartReturnTransactionFails(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* non existent sale transaction */
            assertSame(-1, ezshop.startReturnTransaction(10));
            /* not payed sale transaction */
            assertSame(-1, ezshop.startReturnTransaction(2));


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testStartReturnTransaction(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);
            assertTrue(id > 0);
            assertSame(1, ezshop.getReturnTransactions().size());
            assertTrue(ezshop.getReturnTransactions().containsKey(1));

            /* Return transaction is stored only in the map, not yet in the db */
            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);
            assertSame(1, rt.getId());
            assertSame(1, rt.getSaleTransactionId());
            assertEquals(0.0, rt.getPrice(), 0.00001);
            assertEquals("OPEN", rt.getState());


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testReturnProductThrowsException(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            ezshop.logout();
            /* Throws UnauthorizedException if no user logged */
            assertThrows(UnauthorizedException.class, ()->{ezshop.returnProduct(1,
                    "8008234073496", 5);});

            /* authorized user */
            ezshop.login("john", "5678");

            /* Throws InvalidTransactionIdException if transactionId is null or less or equal to 0 */
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.returnProduct(null,
                    "8008234073496", 5);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.returnProduct(0,
                    "8008234073496", 5);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.returnProduct(-1,
                    "8008234073496", 5);});

            /* Throws InvalidProductCodeException if product code is null, empty or not valid */
            assertThrows(InvalidProductCodeException.class, ()->{ezshop.returnProduct(1,
                    null, 5);});
            assertThrows(InvalidProductCodeException.class, ()->{ezshop.returnProduct(1,
                    "", 5);});
            assertThrows(InvalidProductCodeException.class, ()->{ezshop.returnProduct(1,
                    "123", 5);});

            /* Throws InvalidQuantityException if quantity is less than or equal to zero */
            assertThrows(InvalidQuantityException.class, ()->{ezshop.returnProduct(1,
                    "8008234073496", 0);});
            assertThrows(InvalidQuantityException.class, ()->{ezshop.returnProduct(1,
                    "8008234073496", -1);});


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testReturnProductFails(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            /* return transaction doesn't exist */
            assertFalse(ezshop.returnProduct(10, "8008234073496", 5));
            /* product to return doesn't exist */
            assertFalse(ezshop.returnProduct(1, "3023100003037", 5));
            /* product is not in sale transaction */
            assertFalse(ezshop.returnProduct(1, "8026912833279", 5));
            /* quantity is more than sold */
            assertFalse(ezshop.returnProduct(1, "8008234073496", 20));


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testReturnProduct(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));

            List<TicketEntry> entries = rt.getEntryList();
            assertSame(1, entries.size());
            assertSame(5, entries.get(0).getAmount());
            assertEquals(2.50, rt.getPrice(), 0.00001);

            /* product p2 1 of 2 for 1 euro (with discount) */
            assertTrue(ezshop.returnProduct(1, "8010333001874", 1));
            assertSame(2, entries.size());
            assertSame(1, entries.get(1).getAmount());
            assertEquals(3.50, rt.getPrice(), 0.00001);

            /* return of p1 again 2 units for 1 euro */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 2));
            /* entries are always two */
            assertSame(2, entries.size());
            assertSame(7, entries.get(0).getAmount());
            assertEquals(4.50, rt.getPrice(), 0.00001);


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testEndReturnTransactionThrowsException(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));
            /* product p2 1 of 2 for 1 euro (with discount) */
            assertTrue(ezshop.returnProduct(1, "8010333001874", 1));
            /* return of p1 again 2 units for 1 euro */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 2));

            List<TicketEntry> entries = rt.getEntryList();

            ezshop.logout();
            /* Throws UnauthorizedException if no user logged */
            assertThrows(UnauthorizedException.class, ()->{ezshop.endReturnTransaction(10, true);});

            /* authorized user */
            ezshop.login("john", "5678");

            /* Throws InvalidTransactionIdException if transactionId is null or less or equal to 0 */
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.endReturnTransaction(
                    null, true);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.endReturnTransaction(
                    0, true);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.endReturnTransaction(
                    -1, true);});


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testEndReturnTransactionFails(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));
            /* product p2 1 of 2 for 1 euro (with discount) */
            assertTrue(ezshop.returnProduct(1, "8010333001874", 1));
            /* return of p1 again 2 units for 1 euro */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 2));

            List<TicketEntry> entries = rt.getEntryList();

            /* return transaction doesn't exist */
            assertFalse(ezshop.endReturnTransaction(10, true));


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testEndReturnTransaction(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));
            /* product p2 1 of 2 for 1 euro (with discount) */
            assertTrue(ezshop.returnProduct(1, "8010333001874", 1));
            /* return of p1 again 2 units for 1 euro */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 2));

            List<TicketEntry> entries = rt.getEntryList();

            /********************** BEFORE END RETURN *****************/

            /* products returned */
            ProductType pencil = ezshop.getAllProductTypes().stream()
                    .filter(p -> p.getBarCode().equals("8008234073496"))
                    .findAny()
                    .orElse(null);
            ProductType notebook = ezshop.getAllProductTypes().stream()
                    .filter(p -> p.getBarCode().equals("8010333001874"))
                    .findAny()
                    .orElse(null);

            /* return transaction is closed */
            assertTrue(ezshop.endReturnTransaction(1, true));

            /********************** AFTER END RETURN *****************/

            assertEquals("CLOSED", rt.getState());

            /* return transaction is inserted in DB */
            ReturnTransaction rtDB = db.getReturnTransactions().get(id);
            assertEquals("CLOSED", rtDB.getState());
            assertEquals(4.50, rtDB.getPrice(), 0.00001);

            /* sale transaction is updated */
            SaleTransaction st = db.getSaleTransaction(rt.getSaleTransactionId());
            assertEquals(2.50, st.getPrice(), 0.00001);
            assertSame(2, st.getEntries().size());
            assertSame(3, st.getEntries().get(0).getAmount());
            assertSame(1, st.getEntries().get(1).getAmount());

            List<TicketEntry> returnedProductsDB = rtDB.getEntryList();

            assertSame(2, returnedProductsDB.size());
            assertSame(7, returnedProductsDB.get(0).getAmount());
            assertSame(1, returnedProductsDB.get(1).getAmount());

            /* quantity updated in inventory */
            assertSame(107, pencil.getQuantity());
            assertSame(101, notebook.getQuantity());

            /* quantity in db */
            ProductType pencildb = db.getProducts().stream()
                    .filter(p -> p.getBarCode().equals("8008234073496"))
                    .findAny()
                    .orElse(null);
            ProductType notebookdb = db.getProducts().stream()
                    .filter(p -> p.getBarCode().equals("8010333001874"))
                    .findAny()
                    .orElse(null);

            assertSame(107, pencildb.getQuantity());
            assertSame(101, notebookdb.getQuantity());


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testEndReturnTransactionNoCommit(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            /* return transaction is closed */
            assertTrue(ezshop.endReturnTransaction(1, false));
            assertTrue(ezshop.getReturnTransactions().isEmpty());

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testDeleteReturnTransactionThrowsException(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));

            /* return transaction is closed */
            assertTrue(ezshop.endReturnTransaction(1, true));

            /* no user is logged */
            ezshop.logout();
            /* Throws UnauthorizedException if no user logged */
            assertThrows(UnauthorizedException.class, ()->{ezshop.deleteReturnTransaction(1);});

            ezshop.login("john", "5678");

            /* Throws InvalidTransactionIdException if transactionId is null or less or equal to 0 */
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.deleteReturnTransaction(null);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.deleteReturnTransaction(-1);});
            assertThrows(InvalidTransactionIdException.class, ()->{ezshop.deleteReturnTransaction(0);});


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testDeleteReturnTransactionFails(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));

            /* returns false if return transaction doesn't exists or is not closed */
            assertFalse(ezshop.deleteReturnTransaction(10));
            assertFalse(ezshop.deleteReturnTransaction(1));


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testDeleteReturnTransaction(){
        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));

            /* return transaction is closed */
            assertTrue(ezshop.endReturnTransaction(1, true));

            /* return transaction is deleted */
            assertTrue(ezshop.deleteReturnTransaction(1));
            assertTrue(ezshop.getReturnTransactions().isEmpty());
            assertTrue(db.getReturnTransactions().isEmpty());
            assertTrue(db.getReturnedProducts(1).isEmpty());

            /* sale transaction is updated */
            SaleTransaction st = db.getSaleTransaction(1);
            assertEquals(7.0, st.getPrice(), 0.00001);
            assertSame(2, st.getEntries().size());
            assertSame(10, st.getEntries().get(0).getAmount());

            /* products after delete return */
            ProductType pencil = ezshop.getAllProductTypes().stream()
                    .filter(p -> p.getBarCode().equals("8008234073496"))
                    .findAny()
                    .orElse(null);
            assertSame(100, pencil.getQuantity());

            /* quantity in db */
            ProductType pencildb = db.getProducts().stream()
                    .filter(p -> p.getBarCode().equals("8008234073496"))
                    .findAny()
                    .orElse(null);
            assertSame(100, pencildb.getQuantity());


        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testReturnTransactionCreditCard(){

        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);
            assertTrue(id > 0);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));

            /* return transaction is closed */
            assertTrue(ezshop.endReturnTransaction(1, true));

            /* returns credit card */
            assertEquals(rt.getPrice(), ezshop.returnCreditCardPayment(id,
                    "4485370086510891" ), 0.0001);

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException | InvalidCreditCardException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testReturnTransactionCashPayment(){

        try {
            EZShopDB db = new EZShopDB();
            EZShop ezshop = new EZShop();

            /* authorized user */
            ezshop.login("john", "5678");

            /* Start return transaction */
            int id = ezshop.startReturnTransaction(1);
            assertTrue(id > 0);

            ReturnTransaction rt = ezshop.getReturnTransactions().get(1);

            /* product p1 5 of 10 returned for 2.50 euros */
            assertTrue(ezshop.returnProduct(1, "8008234073496", 5));

            /* return transaction is closed */
            assertTrue(ezshop.endReturnTransaction(1, true));

            /* returns credit card */
            assertEquals(rt.getPrice(), ezshop.returnCashPayment(id), 0.0001);

        } catch (SQLException | InvalidPasswordException | InvalidUsernameException | InvalidTransactionIdException | UnauthorizedException | InvalidProductCodeException | InvalidQuantityException throwables) {
            throwables.printStackTrace();
        }
    }

    @After
    public void resetEZShop(){
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }
}
