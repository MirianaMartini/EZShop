package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestReceivePayment {

    @Before
    public void setEZShop_DB() {
        try {
            EZShopDB db = new EZShopDB();

            db.resetDB();

            User shopManager = new UserImpl(1, "emma", "1234", "ShopManager");
            User cashier = new UserImpl(2, "john", "5678", "Cashier");
            db.insertUser(shopManager);
            db.insertUser(cashier);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testReceiveCashPayment() {
        try {
            EZShop ez= new EZShop();
            EZShopDB db= new EZShopDB();

            //SET DB

            //authorized user for the creation of saleTransaction
            ez.login("emma", "1234");

            int id= ez.startSaleTransaction();
            int id_p=ez.createProductType("milk", "8711600786226", 4, "notes");
            ProductType p1= ez.getProductTypeByBarCode("8711600786226");
            ez.updatePosition(id_p, "12-A-4");
            ez.updateQuantity(id_p, 10);
            ez.addProductToSale(id, "8711600786226", 6);
            ez.endSaleTransaction(id);

            ez.logout();

            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.receiveCashPayment(1, 30);});

            //authorized user
            ez.login("john", "5678"); //cashier

            //EXCEPTION thrown InvalidTransaction
            assertThrows(InvalidTransactionIdException.class, ()->{ez.receiveCashPayment(null, 20);});
            assertThrows(InvalidTransactionIdException.class, ()->{ez.receiveCashPayment(-3, 30);});

            //EXCEPTION thrown InvalidPayment
            assertThrows(InvalidPaymentException.class, ()->{ez.receiveCashPayment(1, 0);});

            //valid - money to return
            double money_returned=ez.receiveCashPayment(id, 30);
            assertNotEquals(-1, money_returned, 0.0001);
            SaleTransaction st = ez.getSaleTransaction(id);
            assertEquals(30.00-st.getPrice(),money_returned, 0.0001);

            //Saletransaction not found - not valid
            assertEquals(-1, ez.receiveCashPayment(id+50, 10), 0.0001);

            //not enough cash - not valid
            assertEquals(-1, ez.receiveCashPayment(id, 10), 0.0001);

        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        } catch (InvalidPaymentException e){
            e.printStackTrace();
        }catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
        }catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testReceiveCreditCardPayment() {
        try {
            EZShop ez= new EZShop();
            EZShopDB db= new EZShopDB();

            //SET DB

            //authorized user to create SaleTransaction ecc
            ez.login("emma", "1234");

            int id= ez.startSaleTransaction();
            int id_p= ez.createProductType("pizza", "8005235078697", 4, "notes");
            ProductType p1= ez.getProductTypeByBarCode("8005235078697");
            ez.updatePosition(id_p, "12-A-5");
            ez.updateQuantity(id_p, 10);
            ez.addProductToSale(id, "8005235078697", 6);
            ez.endSaleTransaction(id);

            ez.logout();


            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.receiveCreditCardPayment(1, "4485370086510891");});

            //authorized user
            ez.login("john", "5678"); //cashier

            //EXCEPTION thrown TransactionId
            assertThrows(InvalidTransactionIdException.class, ()->{ez.receiveCreditCardPayment(null, "4485370086510891");});
            assertThrows(InvalidTransactionIdException.class, ()->{ez.receiveCreditCardPayment(-3, "4485370086510891");});

            //EXCEPTION thrown InvalidCreditCard
            assertThrows(InvalidCreditCardException.class, ()->{ez.receiveCreditCardPayment(1, null);});
            assertThrows(InvalidCreditCardException.class, ()->{ez.receiveCreditCardPayment(1, "");});
            assertThrows(InvalidCreditCardException.class, ()->{ez.receiveCreditCardPayment(1, "4485379086510891");});


            //Saletransaction not found - not valid
            assertFalse(ez.receiveCreditCardPayment(id+50, "4485370086510891"));

            //not enough balance in the credit card - not valid
            assertFalse(ez.receiveCreditCardPayment(id, "5100293991053009"));

            //credit card not registered - not valid
            assertFalse(ez.receiveCreditCardPayment(id, "1234567812345670"));

            //valid
            assertTrue(ez.receiveCreditCardPayment(id, "4485370086510891"));

        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
        }catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        }catch (InvalidCreditCardException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCompleteSaleTransaction() {
        try {
            EZShop ez= new EZShop();
            EZShopDB db= new EZShopDB();

            //SET DB
            //authorized user for the creation of saleTransaction
            ez.login("emma", "1234");

            int id= ez.startSaleTransaction();

            int id_p= ez.createProductType("peluche", "8011483083215", 4, "notes");
            ProductType p1= ez.getProductTypeByBarCode("8011483083215");
            ez.updatePosition(id_p, "12-A-4");
            ez.updateQuantity(id_p, 10);

            ez.addProductToSale(id, "8011483083215", 6);
            ez.applyDiscountRateToProduct(id, "8011483083215", 0.05);
            ez.applyDiscountRateToSale(id, 0.05);

            ez.endSaleTransaction(id);

            //valid - money to return
            double money_returned=ez.receiveCashPayment(id, 30);
            assertNotEquals(-1, money_returned, 0.0001);

            SaleTransaction st = ez.getSaleTransaction(id);
            assertEquals(30.00-st.getPrice(),money_returned, 0.0001);

        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        } catch (InvalidPaymentException e){
            e.printStackTrace();
        }catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
        }catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InvalidDiscountRateException e) {
            e.printStackTrace();
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAbortPaymentAndDeleteSaleTransaction() {
        try {
            EZShop ez= new EZShop();
            EZShopDB db= new EZShopDB();
            //SET DB
            //authorized user for the creation of saleTransaction
            ez.login("emma", "1234");

            int id= ez.startSaleTransaction();

            int id_p= ez.createProductType("pencil", "8004020983710", 4, "notes");
            ProductType p1= ez.getProductTypeByBarCode("8004020983710");
            ez.updatePosition(id_p, "12-A-4");
            ez.updateQuantity(id_p, 10);

            ez.addProductToSale(id, "8004020983710", 6);
            ez.endSaleTransaction(id);

            //money to return (cash amount is less than transaction's price)
            double money_returned=ez.receiveCashPayment(id, 4);

            //Invalid Payment --> Abort payment and delete SaleTransaction
            assertEquals(-1, money_returned, 0.0001);

            //assertNotNull(ez.getSaleTransaction(id));
            assertTrue(ez.deleteSaleTransaction(id));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        } catch (InvalidPaymentException e){
            e.printStackTrace();
        }catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
        }catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InvalidProductIdException e) {
            e.printStackTrace();
        } catch (InvalidLocationException e) {
            e.printStackTrace();
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
