package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.User;
import it.polito.ezshop.data.UserImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestUser {

    @Test
    public void testUser(){

        //CONSTRUCTOR
        User u = new UserImpl(1, "UserName", "PASSword", "Administrator");
        assertNotNull(u);


        //GETTERS

        int id = u.getId();
        assertEquals(1, id);

        String username = u.getUsername();
        assertEquals("UserName", username);

        String pwd = u.getPassword();
        assertEquals("PASSword", pwd);

        String role = u.getRole();
        assertEquals("Administrator", role);


        //SETTERS

        u.setId(2);
        int newId = u.getId();
        assertEquals(2, newId);

        u.setUsername("UsErNaMe");
        String newUsername = u.getUsername();
        assertEquals("UsErNaMe", newUsername);

        u.setPassword("pwd");
        String newPwd = u.getPassword();
        assertEquals("pwd", newPwd);

        u.setRole("Cashier");
        String newRole = u.getRole();
        assertEquals("Cashier", newRole);

    }

}
