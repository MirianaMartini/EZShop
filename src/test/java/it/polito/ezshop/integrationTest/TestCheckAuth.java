package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopDB;
import it.polito.ezshop.data.User;
import it.polito.ezshop.data.UserImpl;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class TestCheckAuth {

    @Before
    public void setEZShop(){
        try {
            EZShopDB db = new EZShopDB();
            User admin = new UserImpl(1, "admin", "123", "Administrator");
            User cashier = new UserImpl(2, "cashier", "123", "Cashier");
            User shop = new UserImpl(3, "shop", "123", "ShopManager");
            User wrong = new UserImpl(4, "wrong", "123", "wrong");
            db.deleteUsers();

            db.insertUser(admin);
            db.insertUser(cashier);
            db.insertUser(shop);
            db.insertUser(wrong);


        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testNull(){

        EZShop ezshop = new EZShop();
        assertFalse(ezshop.checkAuth("Administrator"));
        assertFalse(ezshop.checkAuth("ShopManager"));
        assertFalse(ezshop.checkAuth("Cashier"));
    }

    @Test
    public void testAdministrator(){
        try {
            EZShop ezshop = new EZShop();

            ezshop.login("admin", "123");

            assertTrue(ezshop.checkAuth("Administrator"));
            assertTrue(ezshop.checkAuth("ShopManager"));
            assertTrue(ezshop.checkAuth("Cashier"));
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShopManager(){
        try {
            EZShop ezshop = new EZShop();

            ezshop.login("shop", "123");

            assertFalse(ezshop.checkAuth("Administrator"));
            assertTrue(ezshop.checkAuth("ShopManager"));
            assertTrue(ezshop.checkAuth("Cashier"));
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCashier(){
        try {
            EZShop ezshop = new EZShop();

            ezshop.login("cashier", "123");

            assertFalse(ezshop.checkAuth("Administrator"));
            assertFalse(ezshop.checkAuth("ShopManager"));
            assertTrue(ezshop.checkAuth("Cashier"));
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWrong(){
        try {
            EZShop ezshop = new EZShop();

            ezshop.login("wrong", "123");

            assertFalse(ezshop.checkAuth("Administrator"));
            assertFalse(ezshop.checkAuth("ShopManager"));
            assertFalse(ezshop.checkAuth("Cashier"));
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            e.printStackTrace();
        }
    }

}
