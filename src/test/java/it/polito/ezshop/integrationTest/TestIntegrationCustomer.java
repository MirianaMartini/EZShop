package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class TestIntegrationCustomer {

    @Before
    public void setEZShop(){
        try {
            EZShopDB db = new EZShopDB();
            Customer customer = new CustomerImpl(1, "customer");
            User user = new UserImpl(1, "cashier", "456", "Cashier");

//            db.deleteCustomers();
//
//            db.deleteLoyaltyCards();
//            db.deleteUsers();
            db.resetDB();
            db.insertCustomer(customer);
            db.insertUser(user);
            db.updateCustomerCard(1, "1111111111");
            db.insertLoyaltyCard(new LoyaltyCardImpl("0000000001", true));
            db.insertLoyaltyCard(new LoyaltyCardImpl("0000000002", true));

            //db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void resetDB(){

        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();
            //db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testCreateCustomer(){

        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ()->{ezShop.defineCustomer("Pippo");});

            ezShop.login("cashier", "456");
            assertThrows(InvalidCustomerNameException.class, ()->{ezShop.defineCustomer(null);});
            assertThrows(InvalidCustomerNameException.class, ()->{ezShop.defineCustomer("");});

            assertEquals((Integer)(-1), ezShop.defineCustomer("customer"));
            assertEquals((Integer)(2), ezShop.defineCustomer("Pippo"));

            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidCustomerNameException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testUpdateCustomer(){

        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();
            Customer c = null;
            assertThrows(UnauthorizedException.class, ()->{ezShop.modifyCustomer(1, "new customer", "1111111111");});

            ezShop.login("cashier", "456");
            assertThrows(InvalidCustomerIdException.class, ()->{ezShop.modifyCustomer(null, "new customer", "1111111111");});
            assertThrows(InvalidCustomerIdException.class, ()->{ezShop.modifyCustomer(0, "new customer", "1111111111");});

            assertThrows(InvalidCustomerNameException.class, ()->{ezShop.modifyCustomer(1, null, "1111111111");});
            assertThrows(InvalidCustomerNameException.class, ()->{ezShop.modifyCustomer(1, "", "1111111111");});

            assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyCustomer(1, "new customer", "asd121");});

            assertFalse(ezShop.modifyCustomer(3, "new customer", null)); //No existing Customer
            assertTrue(ezShop.modifyCustomer(1, "new customer", null));
            c = db.getCustomer(1);
            assertEquals("1111111111", c.getCustomerCard());
            assertEquals("new customer", c.getCustomerName());

//            db.closeConnection();
//            db.openConnection();

            assertFalse(ezShop.modifyCustomer(3, "new new customer", "")); //No existing Customer
            assertTrue(ezShop.modifyCustomer(1, "new new customer", ""));
            c = db.getCustomer(1);
            assertNull(c.getCustomerCard());
            assertEquals("new new customer", c.getCustomerName());

//            db.closeConnection();
//            db.openConnection();

            assertFalse(ezShop.modifyCustomer(3, "customer", "2222222222")); //No existing Customer
            assertTrue(ezShop.modifyCustomer(1, "customer", "2222222222"));
            c = db.getCustomer(1);
            assertEquals("2222222222", c.getCustomerCard());
            assertEquals("customer", c.getCustomerName());

            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidCustomerNameException | InvalidCustomerCardException | InvalidCustomerIdException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testDeleteUser(){

        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ()->{ezShop.deleteCustomer(1);});

            ezShop.login("cashier", "456");

            assertThrows(InvalidCustomerIdException.class, ()->{ezShop.deleteCustomer(null);});
            assertThrows(InvalidCustomerIdException.class, ()->{ezShop.deleteCustomer(0);});


            assertTrue(ezShop.deleteCustomer(1));
            assertFalse(ezShop.deleteCustomer(1));

            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidCustomerIdException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetSingleCustomer(){
        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ()->{ezShop.getCustomer(2);});

            ezShop.login("cashier", "456");

            assertThrows(InvalidCustomerIdException.class, ()->{ezShop.getCustomer(null);});
            assertThrows(InvalidCustomerIdException.class, ()->{ezShop.getCustomer(0);});

            assertEquals((Integer)1, ezShop.getCustomer(1).getId());
            assertNull(ezShop.getCustomer(24));

            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidCustomerIdException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testAllCustomers(){
        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ezShop::getAllCustomers);

            ezShop.login("cashier", "456");

            assertEquals(1, ezShop.getAllCustomers().size());
            ezShop.deleteCustomer(1);
            assertEquals(0, ezShop.getAllCustomers().size());
            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidCustomerIdException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateCard(){
        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ezShop::createCard);

            ezShop.login("cashier", "456");

            assertEquals("0000000003", ezShop.createCard());
            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testAttachCardToCustomer(){
        try {
            EZShop ezShop = new EZShop();

            assertThrows(UnauthorizedException.class, ()->{ezShop.attachCardToCustomer("0000000001", 1);});

            ezShop.login("cashier", "456");

            assertThrows(InvalidCustomerCardException.class, ()->{ezShop.attachCardToCustomer("222222asd", 1);});
            assertThrows(InvalidCustomerCardException.class, ()->{ezShop.attachCardToCustomer("", 1);});
            assertThrows(InvalidCustomerCardException.class, ()->{ezShop.attachCardToCustomer(null, 1);});

            assertThrows(InvalidCustomerIdException.class, ()->{ezShop.attachCardToCustomer("0000000001", null);});
            assertThrows(InvalidCustomerIdException.class, ()->{ezShop.attachCardToCustomer("0000000001", 0);});

            assertFalse(ezShop.attachCardToCustomer("0000000001", 1)); //Card already assigned
            assertFalse(ezShop.attachCardToCustomer("0000000001", 45)); //Customer doesn't exist
            String card = ezShop.createCard();
            assertTrue(ezShop.attachCardToCustomer(card, 1));

            assertEquals("0000000003", ezShop.getCustomer(1).getCustomerCard());


        } catch (InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidCustomerIdException | InvalidCustomerCardException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testModifyPointsOnCard(){
        try {
            EZShop ezShop = new EZShop();
            EZShopDB db = new EZShopDB();

            assertThrows(UnauthorizedException.class, ()->{ezShop.modifyPointsOnCard("1111111111", 50);});

            ezShop.login("cashier", "456");

            assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyPointsOnCard(null, 50);});
            assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyPointsOnCard("", 50);});
            assertThrows(InvalidCustomerCardException.class, ()->{ezShop.modifyPointsOnCard("asd456", 50);});

            assertFalse(ezShop.modifyPointsOnCard("3333333333", 50)); //no Customer with this Card
            assertTrue(ezShop.modifyPointsOnCard("1111111111", 50));

            assertFalse(ezShop.modifyPointsOnCard("1111111111", -60)); //No enough points
            assertTrue(ezShop.modifyPointsOnCard("1111111111", -30));

            assertEquals((Integer)20, ezShop.getCustomer(1).getPoints());

            //db.closeConnection();

        } catch (SQLException | InvalidUsernameException | InvalidPasswordException | UnauthorizedException | InvalidCustomerIdException | InvalidCustomerCardException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
