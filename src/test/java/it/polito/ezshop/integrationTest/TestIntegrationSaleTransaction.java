package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.*;

import static org.junit.Assert.*;

import java.sql.SQLException;

public class TestIntegrationSaleTransaction {

    @Before
    public void setEZShop(){
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

            User shopManager = new UserImpl(1, "emma", "1234", "ShopManager");
            User cashier = new UserImpl(2, "john", "5678", "Cashier");

            ProductType product1 = new ProductTypeImpl(1, "Pencil",
                    "8008234073496", 0.50, "Writes on paper");
            ProductType product2 = new ProductTypeImpl(2, "Rubber",
                    "4007817524503", 1.00, "Erases perfectly pencils signs");
            ProductType product3 = new ProductTypeImpl(3, "Notebook",
                    "6973205090043", 0.70, "Good at taking notes and drawing landscapes");
            ProductType product4 = new ProductTypeImpl(4, "Black_Marker",
                    "4902778916117", 1.99, "Leave your mark wherever you want!");
            db.insertUser(shopManager);
            db.insertUser(cashier);
            db.insertProductTypeDB((ProductTypeImpl) product1);
            db.insertProductTypeDB((ProductTypeImpl) product2);
            db.insertProductTypeDB((ProductTypeImpl) product3);
            db.insertProductTypeDB((ProductTypeImpl) product4);
            db.updateQuantityDB(1,100);
            db.updateQuantityDB(2,100);
            db.updateQuantityDB(3,100);
            db.updateQuantityDB(4,100);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testStartSaleTransaction() {
        try {
            EZShop ez = new EZShop();
            int counter = ez.getSaleTransactionsCounter();
            //Throws Unauthorized Exception if none is logged or someone is logged with wrong role
            assertThrows(UnauthorizedException.class, ()->{ez.startSaleTransaction();});
            //Check right role (Cashier)
            ez.login("john", "5678");

            assertEquals(new Integer(counter+1), ez.startSaleTransaction());
            assertEquals(new Integer(counter+2), ez.startSaleTransaction());

        } catch (InvalidUsernameException | InvalidPasswordException | UnauthorizedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAddProductToSale(){
        try {
            EZShop ez = new EZShop();
            int counter = ez.getSaleTransactionsCounter() + 1;
            SaleTransactionImpl st;

            assertThrows(UnauthorizedException.class, ()->{ez.addProductToSale(1,
                    "8008234073496", 3);});

            //Check right role (Cashier)
            ez.login("emma", "1234");
            //Create a new SaleTransaction to run the test correctly
            assertEquals(new Integer(counter), ez.startSaleTransaction());

            assertThrows(InvalidTransactionIdException.class, ()->{ez.addProductToSale(-1,
                    "8008234073496", 3);});
            assertThrows(InvalidProductCodeException.class, ()->{ez.addProductToSale(1,
                    null, 3);});
            assertThrows(InvalidQuantityException.class, ()->{ez.addProductToSale(1,
                    "8008234073496", -1);});

            ez.addProductToSale(counter, "8008234073496", 3);
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertNotNull(st);
            assertEquals(1, st.getEntries().size());
            assertEquals(1.50, st.getPrice(), 0.00001);

            /*Check if quantity of the ProductType in SaleTransaction recorded on the DB is correct, so
              the same as before the addiction, because the DB quantity of all ProductTypes in SaleTransaction will be
              updated once the transaction switches to "CLOSED" state */
            assertEquals(new Integer(97), ez.getProductTypeByBarCode("8008234073496").getQuantity());

        } catch (InvalidTransactionIdException | InvalidProductCodeException | InvalidQuantityException
                | InvalidPasswordException | InvalidUsernameException | UnauthorizedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeleteProductFromSale(){
        try {
            EZShop ez = new EZShop();
            int counter = ez.getSaleTransactionsCounter() + 1;
            SaleTransactionImpl st;

            assertThrows(UnauthorizedException.class, () -> { ez.addProductToSale(1,
                        "8008234073496", 3); });

            //Check right role (Cashier)
            ez.login("emma", "1234");
            //Create a new SaleTransaction to run the test correctly
            assertEquals(new Integer(counter), ez.startSaleTransaction());

            assertThrows(InvalidTransactionIdException.class, () -> { ez.addProductToSale(-1,
                        "8008234073496", 3); });
            assertThrows(InvalidProductCodeException.class, () -> { ez.addProductToSale(1,
                        null, 3); });
            assertThrows(InvalidQuantityException.class, () -> { ez.addProductToSale(1,
                        "8008234073496", -1); });

            //Adding 2 different types of product
            assertTrue(ez.addProductToSale(counter, "8008234073496", 3));
            assertTrue(ez.addProductToSale(counter, "4007817524503", 5));
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertNotNull(st);
            assertEquals(2, st.getEntries().size());
            assertEquals(6.50, st.getPrice(), 0.00001);

            //Removing a number of pieces of a certain ProductType from SaleTransaction
            assertTrue(ez.deleteProductFromSale(counter,"4007817524503", 1));
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertNotNull(st);
            assertEquals(2, st.getEntries().size());
            assertEquals(5.50, st.getPrice(), 0.00001);
            assertEquals(new Integer(97), ez.getProductTypeByBarCode("8008234073496").getQuantity());
            assertEquals(new Integer(96), ez.getProductTypeByBarCode("4007817524503").getQuantity());

            //Removing ALL the pieces of a certain ProductType from SaleTransaction
            assertTrue(ez.deleteProductFromSale(counter,"8008234073496", 3));
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertNotNull(st);
            assertEquals(1, st.getEntries().size());
            assertEquals(4.00, st.getPrice(), 0.00001);
            assertEquals(new Integer(100), ez.getProductTypeByBarCode("8008234073496").getQuantity());
            assertEquals(new Integer(96), ez.getProductTypeByBarCode("4007817524503").getQuantity());

        } catch (InvalidTransactionIdException | InvalidProductCodeException | InvalidQuantityException
                | InvalidPasswordException | InvalidUsernameException | UnauthorizedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testApplyDiscountRateToProduct(){
        try {
            EZShop ez = new EZShop();
            int counter = ez.getSaleTransactionsCounter() + 1;
            SaleTransactionImpl st;

            assertThrows(UnauthorizedException.class, () -> { ez.addProductToSale(1,
                    "8008234073496", 3); });

            //Check right role (Cashier)
            ez.login("john", "5678");
            //Create a new SaleTransaction to run the test correctly
            assertEquals(new Integer(counter), ez.startSaleTransaction());


            assertThrows(InvalidTransactionIdException.class, () -> { ez.addProductToSale(-1,
                    "8008234073496", 3); });
            assertThrows(InvalidProductCodeException.class, () -> { ez.addProductToSale(1,
                    null, 3); });
            assertThrows(InvalidDiscountRateException.class, () -> { ez.applyDiscountRateToProduct(1,
                    "8008234073496", -0.10);});

            //Adding 2 different types of product
            assertTrue(ez.addProductToSale(counter, "8008234073496", 3));
            assertTrue(ez.addProductToSale(counter, "4007817524503", 5));
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertNotNull(st);
            assertEquals(2, st.getEntries().size());
            assertEquals(6.50, st.getPrice(), 0.00001);

            assertTrue(ez.applyDiscountRateToProduct(counter, "8008234073496", 0.10));
            assertEquals(0.10, st.getEntries().get(0).getDiscountRate(), 0.00001);
            assertEquals(0.00, st.getEntries().get(1).getDiscountRate(), 0.00001);
            assertEquals(6.35, st.getPrice(), 0.00001);

        } catch (InvalidTransactionIdException | InvalidProductCodeException
                | InvalidDiscountRateException | InvalidPasswordException | InvalidUsernameException
                | UnauthorizedException | InvalidQuantityException e){
            fail(e.getMessage());
        }
    }

    @Test
    public void testApplyDiscountRateToOpenedSale(){
        try {
            EZShop ez = new EZShop();
            int counter = ez.getSaleTransactionsCounter() +  1;
            SaleTransactionImpl st;

            assertThrows(UnauthorizedException.class, () -> { ez.addProductToSale(1,
                        "8008234073496", 3); });

            //Check right role (Cashier)
            ez.login("john", "5678");
            //Create a new SaleTransaction to run the test correctly
            assertEquals(new Integer(counter), ez.startSaleTransaction());


            assertThrows(InvalidTransactionIdException.class, () -> { ez.addProductToSale(-1,
                        "8008234073496", 3); });
            assertThrows(InvalidProductCodeException.class, () -> { ez.addProductToSale(1,
                        null, 3); });
            assertThrows(InvalidDiscountRateException.class, () -> { ez.applyDiscountRateToSale(1,
                    -0.10); });

            //Adding 2 different types of product
            assertTrue(ez.addProductToSale(counter, "8008234073496", 3));
            assertTrue(ez.addProductToSale(counter, "4007817524503", 5));
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertNotNull(st);
            assertEquals(2, st.getEntries().size());
            assertEquals(6.50, st.getPrice(), 0.00001);

            //Applying Discount rate to the whole SaleTransaction
            assertTrue(ez.applyDiscountRateToSale(counter, 0.10));
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertEquals(5.85, st.getPrice(), 0.00001);

        } catch (InvalidTransactionIdException | InvalidProductCodeException
                | InvalidDiscountRateException | InvalidPasswordException | InvalidUsernameException
                | UnauthorizedException | InvalidQuantityException e){
            fail(e.getMessage());
        }
    }

    @Test
    public void testApplyDiscountRateToClosedSale(){
        try {
            EZShop ez = new EZShop();
            EZShopDB db = new EZShopDB();
            int counter = ez.getSaleTransactionsCounter() + 1;
            SaleTransactionImpl st;

            assertThrows(UnauthorizedException.class, () -> { ez.addProductToSale(1,
                    "8008234073496", 3); });

            //Check right role (Cashier)
            ez.login("john", "5678");
            //Create a new SaleTransaction to run the test correctly
            assertEquals(new Integer(counter), ez.startSaleTransaction());


            assertThrows(InvalidTransactionIdException.class, () -> { ez.addProductToSale(-1,
                    "8008234073496", 3); });
            assertThrows(InvalidProductCodeException.class, () -> { ez.addProductToSale(1,
                    null, 3); });
            assertThrows(InvalidDiscountRateException.class, () -> { ez.applyDiscountRateToSale(1,
                    -0.10); });

            //Adding 2 different types of product
            assertTrue(ez.addProductToSale(counter, "8008234073496", 3));
            assertTrue(ez.addProductToSale(counter, "4007817524503", 5));
            assertTrue(ez.endSaleTransaction(counter));
            st = (SaleTransactionImpl) db.getSaleTransactions().get(counter);
            assertTrue(st.getState().equals("CLOSED"));

            //Applying Discount rate to the whole SaleTransaction
            assertTrue(ez.applyDiscountRateToSale(counter, 0.10));

            st = (SaleTransactionImpl) db.getSaleTransactions().get(counter);
            assertEquals(5.85, st.getPrice(), 0.00001);


        } catch (SQLException | InvalidTransactionIdException | InvalidProductCodeException
                | InvalidDiscountRateException | InvalidPasswordException | InvalidUsernameException
                | UnauthorizedException | InvalidQuantityException e){
            fail(e.getMessage());
        }
    }

    @Test
    public void testComputePointsForOpenedSale(){
        try {
            EZShop ez = new EZShop();
            int counter = ez.getSaleTransactionsCounter() +  1;
            SaleTransactionImpl st;

            assertThrows(UnauthorizedException.class, () -> { ez.addProductToSale(1,
                    "8008234073496", 3); });

            //Check right role (Cashier)
            ez.login("john", "5678");
            //Create a new SaleTransaction to run the test correctly
            assertEquals(new Integer(counter), ez.startSaleTransaction());

            assertThrows(InvalidTransactionIdException.class, () -> { ez.addProductToSale(-1,
                    "8008234073496", 3); });
            assertThrows(InvalidProductCodeException.class, () -> { ez.addProductToSale(1,
                    null, 3); });
            assertThrows(InvalidDiscountRateException.class, () -> { ez.applyDiscountRateToSale(1,
                    -0.10); });

            //Return -1 if SaleTransaction with a certain transactionId does not exist.
            assertEquals(-1, ez.computePointsForSale(counter + 1));

            //Adding 3 different types of product
            assertTrue(ez.addProductToSale(counter, "8008234073496", 4));
            assertTrue(ez.addProductToSale(counter, "4007817524503", 6));
            assertTrue(ez.addProductToSale(counter, "4902778916117", 1));
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertNotNull(st);
            //Check that 9.99 = 0 points and 10.00 = 1 point
            assertEquals(3, st.getEntries().size());
            assertEquals(9.99, st.getPrice(), 0.00001);
            assertEquals(0,ez.computePointsForSale(counter));

            assertTrue(ez.addProductToSale(counter, "6973205090043", 5));
            assertTrue(ez.deleteProductFromSale(counter, "4902778916117", 1));
            assertTrue(ez.deleteProductFromSale(counter, "8008234073496", 3));
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            assertEquals(3, st.getEntries().size());
            assertEquals(10.00, st.getPrice(), 0.00001);
            assertEquals(1,ez.computePointsForSale(counter));

        } catch (InvalidTransactionIdException | InvalidProductCodeException
                | InvalidPasswordException | InvalidUsernameException
                | UnauthorizedException | InvalidQuantityException e){
            fail(e.getMessage());
        }
    }

    @Test
    public void testComputePointsForClosedSale(){
        try {
            EZShop ez = new EZShop();
            EZShopDB db = new EZShopDB();
            int counter = ez.getSaleTransactionsCounter() +  1;
            SaleTransactionImpl st;

            assertThrows(UnauthorizedException.class, () -> { ez.addProductToSale(1,
                    "8008234073496", 3); });

            //Check right role (Cashier)
            ez.login("john", "5678");
            //Create a new SaleTransaction to run the test correctly
            assertEquals(new Integer(counter), ez.startSaleTransaction());

            assertThrows(InvalidTransactionIdException.class, () -> { ez.addProductToSale(-1,
                    "8008234073496", 3); });
            assertThrows(InvalidProductCodeException.class, () -> { ez.addProductToSale(1,
                    null, 3); });
            assertThrows(InvalidDiscountRateException.class, () -> { ez.applyDiscountRateToSale(1,
                    -0.10); });

            //Adding 3 different types of product
            assertTrue(ez.addProductToSale(counter, "8008234073496", 10));
            assertTrue(ez.addProductToSale(counter, "4007817524503", 10));
            assertTrue(ez.addProductToSale(counter, "4902778916117", 10));

            //Change balanceId to avoid DB errors
            st = (SaleTransactionImpl) ez.getSaleTransactionInformations(counter);
            //ez.setSaleTransactionBalanceId(counter, st.getBalanceId()+1);
            assertTrue(ez.endSaleTransaction(counter));
            st = (SaleTransactionImpl) db.getSaleTransaction(counter);
            assertNotNull(st);
            assertEquals("CLOSED", st.getState());

            assertEquals(3, st.getEntries().size());
            assertEquals(34.90, st.getPrice(), 0.00001);
            assertEquals(3,ez.computePointsForSale(counter));


        } catch (SQLException | InvalidTransactionIdException | InvalidProductCodeException
                | InvalidPasswordException | InvalidUsernameException
                | UnauthorizedException | InvalidQuantityException e){
            fail(e.getMessage());
        }
    }

    @After
    public void resetEZShop(){
        try {
            EZShop ez = new EZShop();
            EZShopDB db = new EZShopDB();
            db.resetDB();

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }
}