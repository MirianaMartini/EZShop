package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class TestReturnPayment {

    @BeforeClass
    public static void setEZShop_DB() {
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
    public void testReturnCashPayment() {
        try {
            EZShop ez= new EZShop();
            EZShopDB db =new EZShopDB();

            //SET DB

            //authorized user to create Return Transaction ecc
            ez.login("emma", "1234");

            int id= ez.startSaleTransaction();
            int id_p=ez.createProductType("milk", "8711600786226", 4, "notes");
            ProductType p1= ez.getProductTypeByBarCode("8711600786226");
            ez.updatePosition(id_p, "12-A-4");
            ez.updateQuantity(id_p, 10);
            ez.addProductToSale(id, "8711600786226", 6);
            ez.endSaleTransaction(id);
            ez.receiveCashPayment(id, 180.00);
            int id_r=ez.startReturnTransaction(id);
            ez.returnProduct(id_r, "8711600786226", 4);//money_to_return=4(amount)*4(PricePerUnit)=16
            double money_to_return= 16;

            ez.logout();

            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.returnCashPayment(1);});

            //authorized user
            ez.login("john", "5678"); //cashier

            //EXCEPTION thrown InvalidTransactionId
            assertThrows(InvalidTransactionIdException.class, ()->{ez.returnCashPayment(null);});
            assertThrows(InvalidTransactionIdException.class, ()->{ez.returnCashPayment(-3);});


            //The return transaction is not closed - not valid
            assertEquals(-1, ez.returnCashPayment(id_r), 0.0001);

            //Others Set on DB
            ez.logout();
            //authorized user to create Return Transaction ecc
            ez.login("emma", "1234");
            ez.endReturnTransaction(id_r, true);

            ez.logout();


            //authorized user
            ez.login("john", "5678"); //cashier

            //The return Transaction not exists - not valid
            assertEquals(-1, ez.returnCashPayment(id_r+40), 0.0001);

            //return the money to return - valid
            assertEquals(money_to_return, ez.returnCashPayment(id_r), 0.0001);

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
        }catch (InvalidPaymentException e) {
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
    public void testReturnCreditCardPayment() {
        try {
            EZShop ez= new EZShop();
            EZShopDB db= new EZShopDB();

            //SET DB
            //authorized user for create ReturnTransaction, ecc
            ez.login("emma", "1234");

            int id= ez.startSaleTransaction();
            int id_p= ez.createProductType("pizza", "8005235078697", 4, "notes");
            ProductType p1= ez.getProductTypeByBarCode("8005235078697");
            ez.updatePosition(id_p, "12-A-5");
            ez.updateQuantity(id_p, 10);
            ez.addProductToSale(id, "8005235078697", 6);
            ez.endSaleTransaction(id);
            ez.receiveCreditCardPayment(id,"4485370086510891" );
            int id_r=ez.startReturnTransaction(id);
            ez.returnProduct(id_r, "8005235078697", 4); //money_to_return=4(amount)*4(PricePerUnit)=16
            double money_to_return=16;

            ez.logout();

            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.returnCreditCardPayment(1, "4485370086510891");});

            //authorized user
            ez.login("john", "5678"); //cashier

            //EXCEPTION thrown InvalidTransactionId
            assertThrows(InvalidTransactionIdException.class, ()->{ez.returnCreditCardPayment(null, "4485370086510891");});
            assertThrows(InvalidTransactionIdException.class, ()->{ez.returnCreditCardPayment(-3, "4485370086510891");});

            //EXCEPTION thrown InvalidCreditCard
            assertThrows(InvalidCreditCardException.class, ()->{ez.returnCreditCardPayment(1, null);});
            assertThrows(InvalidCreditCardException.class, ()->{ez.returnCreditCardPayment(1, "");});
            assertThrows(InvalidCreditCardException.class, ()->{ez.returnCreditCardPayment(1, "4485379086510891");});


            //The ReturnTransaction is not closed - not valid
            assertEquals(-1, ez.returnCreditCardPayment(id_r, "4485370086510891" ), 0.0001);

            //Others set on DB
            ez.logout();
            //authorized user for EndReturnTransaction, ecc
            ez.login("emma", "1234");

            ez.endReturnTransaction(id_r, true);
            ez.logout();

            //authorized user
            ez.login("john", "5678"); //cashier

            //ReturnTransaction not exists - not valid
            assertEquals(-1, ez.returnCreditCardPayment(id_r+40, "4485370086510891" ), 0.0001);

            //Credit Card not registered - not valid
            assertEquals(-1, ez.returnCreditCardPayment(id_r, "1234567812345670" ), 0.0001);

            //valid
            assertEquals(money_to_return, ez.returnCreditCardPayment(id_r, "4485370086510891" ), 0.0001);

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
