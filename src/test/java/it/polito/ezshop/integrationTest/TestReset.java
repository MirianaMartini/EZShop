package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import static org.junit.Assert.*;

public class TestReset {

    @Before
    public void init(){

        try {
            EZShopDB db = new EZShopDB();
            User admin = new UserImpl(1, "admin", "123", "Administrator");
            User cashier = new UserImpl(2, "cashier", "123", "Cashier");
            User shop = new UserImpl(3, "shop", "123", "ShopManager");

            Customer customer = new CustomerImpl(1, "customer");

            ProductTypeImpl product = new ProductTypeImpl(1, "prodotto", "1234567890", 10.0, "");

            //db.resetDB();

            db.insertUser(admin);
            db.insertUser(cashier);
            db.insertUser(shop);

            db.insertCustomer(customer);

            db.insertLoyaltyCard(new LoyaltyCardImpl("1111111111", true));
            db.insertLoyaltyCard(new LoyaltyCardImpl("2222222222", false));

            db.insertProductTypeDB(product);

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
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testReset(){
        EZShop ezshop = new EZShop();
        ezshop.reset();
    }

}
