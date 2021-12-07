package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopDB;
import it.polito.ezshop.data.User;
import it.polito.ezshop.data.UserImpl;
import it.polito.ezshop.exceptions.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;

public class TestIntegrationUser {

    @Before
    public void setEZShop(){
        try {
            EZShopDB db = new EZShopDB();
            User admin = new UserImpl(1, "admin", "123", "Administrator");
            User cashier = new UserImpl(2, "cashier", "456", "Cashier");
            User shop = new UserImpl(3, "shop", "123", "ShopManager");

            db.resetDB();

            db.insertUser(admin);
            db.insertUser(cashier);
            db.insertUser(shop);

            //db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateUser(){
        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(InvalidUsernameException.class, ()->{ezShop.createUser(null, "pwd", "ShopManager");});
            assertThrows(InvalidUsernameException.class, ()->{ezShop.createUser("", "pwd", "ShopManager");});

            assertThrows(InvalidPasswordException.class, ()->{ezShop.createUser("username", null, "ShopManager");});
            assertThrows(InvalidPasswordException.class, ()->{ezShop.createUser("username", "", "ShopManager");});

            assertThrows(InvalidRoleException.class, ()->{ezShop.createUser("username", "pwd", null);});
            assertThrows(InvalidRoleException.class, ()->{ezShop.createUser("username", "pwd", "");});
            assertThrows(InvalidRoleException.class, ()->{ezShop.createUser("username", "pwd", "shop manager");});


            assertEquals((Integer)(-1), ezShop.createUser("admin", "123", "Administrator"));
            assertEquals((Integer)(4), ezShop.createUser("username", "pwd", "ShopManager"));

            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | InvalidRoleException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testDeleteUser(){

        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ()->{ezShop.deleteUser(2);});

            ezShop.login("cashier", "456");
            assertThrows(UnauthorizedException.class, ()->{ezShop.deleteUser(2);});

            ezShop.login("admin", "123");

            assertThrows(InvalidUserIdException.class, ()->{ezShop.deleteUser(null);});
            assertThrows(InvalidUserIdException.class, ()->{ezShop.deleteUser(0);});


            assertTrue(ezShop.deleteUser(2));
            assertFalse(ezShop.deleteUser(2));

            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | InvalidUserIdException | UnauthorizedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetAllUsers(){

        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ezShop::getAllUsers);

            ezShop.login("cashier", "456");
            assertThrows(UnauthorizedException.class, ezShop::getAllUsers);

            ezShop.login("admin", "123");

            assertEquals(3, ezShop.getAllUsers().size());
            ezShop.deleteUser(1);
            ezShop.deleteUser(2);
            ezShop.deleteUser(3);
            assertEquals(0, ezShop.getAllUsers().size());


            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidUserIdException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetSingleUser(){
        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ()->{ezShop.getUser(2);});

            ezShop.login("cashier", "456");
            assertThrows(UnauthorizedException.class, ()->{ezShop.getUser(2);});

            ezShop.login("admin", "123");

            assertThrows(InvalidUserIdException.class, ()->{ezShop.getUser(null);});
            assertThrows(InvalidUserIdException.class, ()->{ezShop.getUser(0);});

            assertEquals((Integer)2, ezShop.getUser(2).getId());
            assertNull(ezShop.getUser(24));

            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidUserIdException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateUserRights(){
        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ()->{ezShop.updateUserRights(3, "Administrator");});

            ezShop.login("cashier", "456");
            assertThrows(UnauthorizedException.class, ()->{ezShop.updateUserRights(3, "Administrator");});

            ezShop.login("admin", "123");

            assertThrows(InvalidRoleException.class, ()->{ezShop.updateUserRights(3, null);});
            assertThrows(InvalidRoleException.class, ()->{ezShop.updateUserRights(3, "");});
            assertThrows(InvalidRoleException.class, ()->{ezShop.updateUserRights(3, "admin");});

            assertThrows(InvalidUserIdException.class, ()->{ezShop.updateUserRights(null, "Administrator");});
            assertThrows(InvalidUserIdException.class, ()->{ezShop.updateUserRights(0, "Administrator");});

            assertFalse(ezShop.updateUserRights(23, "Administrator"));
            assertTrue(ezShop.updateUserRights(3, "Administrator"));
            assertEquals("Administrator", ezShop.getUser(3).getRole());

           // db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidUserIdException | InvalidRoleException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void testLogin(){
        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(InvalidUsernameException.class, ()->{ezShop.login(null, "123");});
            assertThrows(InvalidUsernameException.class, ()->{ezShop.login("", "123");});

            assertThrows(InvalidPasswordException.class, ()->{ezShop.login("admin", null);});
            assertThrows(InvalidPasswordException.class, ()->{ezShop.login("admin", "");});

            assertNull(ezShop.login("admin", "456"));
            assertEquals((Integer) 1, ezShop.login("admin", "123").getId());


            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testLogout(){
        try {
            EZShop ezShop = new EZShop();

            assertFalse(ezShop.logout());
            ezShop.login("admin", "123");
            assertTrue(ezShop.logout());


        } catch ( InvalidUsernameException | InvalidPasswordException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void resetEZShop() {
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();
            //db.closeConnection();
        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

}
