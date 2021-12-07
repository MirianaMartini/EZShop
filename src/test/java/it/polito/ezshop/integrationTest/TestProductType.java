package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class TestProductType {

    @Before
    public void setEZshopDB() {
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
    public void testCreateProduct() {
        try {
            EZShop ez = new EZShop();
            EZShopDB db= new EZShopDB();

            //no user logged
            assertThrows(UnauthorizedException.class, ()->{ez.createProductType("milk", "8005235078697", 3.50, "notes");});

            //unauthorized user
            ez.login("john", "5678"); //cashier
            assertThrows(UnauthorizedException.class, ()->{ez.createProductType("milk", "8005235078697", 3.50, "notes");});

            //authorized user
            ez.login("emma", "1234");

            //EXCEPTION thrown InvalidProductDecsription
            assertThrows(InvalidProductDescriptionException.class, () -> {ez.createProductType("", "8005235078697", 3.50, "notes");});
            assertThrows(InvalidProductDescriptionException.class, () -> {ez.createProductType(null, "8005235078697", 3.50, "notes");});

            //EXCEPTION thrown InvalidProductCode
            assertThrows(InvalidProductCodeException.class, () -> {ez.createProductType("milk", null, 3.50, "notes");});
            assertThrows(InvalidProductCodeException.class, () -> {ez.createProductType("milk", " ", 3.50, "notes");});
            assertThrows(InvalidProductCodeException.class, () -> {ez.createProductType("milk", "8005235078695", 3.50, "notes");});

            ////EXCEPTION thrown InvalidPricePerUnit
            assertThrows(InvalidPricePerUnitException.class, () -> {ez.createProductType("milk", "8005235078697", 0, "notes");});


            int id = ez.createProductType("milk", "8005235078697", 3.50, "notes");
            ProductType p = ez.getProductTypeByBarCode("8005235078697");
            int id_p = p.getId();

            //Product creation - valid
            assertEquals(id_p, id);

            //same barcode of an already exists product - not valid
            assertTrue(ez.createProductType("pizza", "8005235078697", 5.50, "notes").equals(-1));


        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        }catch (SQLException throwables) {
            fail(throwables.getMessage());
        }

    }

    @Test
    public void testUpdateProduct() {
        try {
            EZShop ez = new EZShop();
            EZShopDB db = new EZShopDB();

            //no user logged
            assertThrows(UnauthorizedException.class, ()->{ez.updateProduct(1, "new tipology of milk", "8005235078697", 6.60, "new notes");});

            //unauthorized user
            ez.login("john", "5678"); //cashier
            assertThrows(UnauthorizedException.class, ()->{ez.updateProduct(1, "new tipology of milk", "8005235078697", 6.60, "new notes");});

            //authorized user
            ez.login("emma", "1234");

            //EXCEPTION thrown InvalidProductId
            assertThrows(InvalidProductIdException.class, () -> {ez.updateProduct(-5, "new milk", "8005235078697", 3.00, "new notes");});
            assertThrows(InvalidProductIdException.class, () -> {ez.updateProduct(null, "new milk", "8005235078697", 3.00, "new notes");});

            //EXCEPTION thrown InvalidProductDescription
            assertThrows(InvalidProductDescriptionException.class, () -> {ez.updateProduct(3, null, "8005235078697", 3.00, "new notes");});
            assertThrows(InvalidProductDescriptionException.class, () -> {ez.updateProduct(3, "", "8005235078697", 3.00, "new notes");});

            //EXCEPTION thrown InvalidProductCode
            assertThrows(InvalidProductCodeException.class, () -> {ez.updateProduct(3, "milk", null, 3.00, "new notes");});
            assertThrows(InvalidProductCodeException.class, () -> {ez.updateProduct(3, "milk", "", 3.00, "new notes");});
            assertThrows(InvalidProductCodeException.class, () -> {ez.updateProduct(3, "milk", "8005235078695", 3.00, "new notes");});

            //EXCEPTION thrown InvalidPricePerUnit
            assertThrows(InvalidPricePerUnitException.class, () -> {ez.updateProduct(3, "milk", "8005235078697", -6.60, "new notes");});


            int p1= ez.createProductType("milk", "8711600786226", 3.50, "notes");
            int p2= ez.createProductType("pizza", "8005235078697", 5.60, "notes");

            //new price > 0 - valid
            assertTrue(ez.updateProduct(p1, "milk", "8711600786226", 5, "notes"));

            //Product totally updated - valid
            assertTrue(ez.updateProduct(p1, "new tipology of milk", "8004020983710", 6.60, "new notes"));

            //Same barcode of an exist product - not valid
            assertFalse(ez.updateProduct(p1, "pencil", "8005235078697", 1.50, "notes"));

            //Id not found - not valid
            assertFalse(ez.updateProduct(p2+40, "milk", "8711600786226", 6.60, "new notes"));

        } catch (InvalidProductIdException e) {
            e.printStackTrace();
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    public void testDeleteProduct() {
       try {
           EZShop ez = new EZShop();
           EZShopDB db =new EZShopDB();

            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.deleteProductType(1);});

            //unauthorized user
            ez.login("john", "5678"); //cashier
            assertThrows(UnauthorizedException.class, () -> {ez.deleteProductType(1);});

            //authorized user
            ez.login("emma", "1234");

            //EXCEPTION thrown InvalidProductId
            assertThrows(InvalidProductIdException.class, () -> {ez.deleteProductType(-5);});
            assertThrows(InvalidProductIdException.class, () -> {ez.deleteProductType(null);});


            ez.createProductType("milk", "8711600786226", 3.50, "notes");  //creation of a valid product

            ProductType p=ez.getProductTypeByBarCode("8711600786226");


            //Id not found - not valid
            assertFalse(ez.deleteProductType(4));

            //Id - valid
            assertTrue(ez.deleteProductType(p.getId()));


        } catch (InvalidProductIdException e) {
            e.printStackTrace();
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
           e.printStackTrace();
       } catch (InvalidPasswordException e) {
           e.printStackTrace();
       } catch (SQLException throwables) {
           throwables.printStackTrace();
       }
    }

    @Test
    public void testGetAllProducts() {
        try {
            EZShop ez = new EZShop();
            EZShopDB db= new EZShopDB();

            //SET DB
            //authorized user for the creation of new products
            ez.login("emma", "1234");

            List<ProductTypeImpl> inventory = new ArrayList<>();

            ez.createProductType("milk", "8005235078697", 3.50, "notes"); //create one product
            ProductTypeImpl p1 = new ProductTypeImpl(1, "milk", "8005235078697", 3.50, "notes");
            inventory.add(p1);

            ez.createProductType("pizza", "8004020983710", 5.50, "notes"); //create another product
            ProductTypeImpl p2 = new ProductTypeImpl(2, "pizza", "8004020983710", 5.50, "notes");
            inventory.add(p2);

            ez.logout();

            //Use method GetAllProducts
            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.getAllProductTypes();});

            //authorized user
            ez.login("john", "5678"); //cashier

            //Get products
            assertEquals(2, ez.getAllProductTypes().size());
            assertEquals(inventory.toString(), ez.getAllProductTypes().toString());

        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    public void testGetProductByBarcode() {
        try {
            EZShop ez = new EZShop();
            EZShopDB db= new EZShopDB();

            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.getProductTypeByBarCode("8711600786226");});

            //unauthorized user
            ez.login("john", "5678"); //cashier
            assertThrows(UnauthorizedException.class, () -> {ez.getProductTypeByBarCode("8711600786226");});

            //authorized user
            ez.login("emma", "1234");

            //EXCEPTION thrown InvalidProductCode
            assertThrows(InvalidProductCodeException.class, () -> {ez.getProductTypeByBarCode(null);});
            assertThrows(InvalidProductCodeException.class, () -> {ez.getProductTypeByBarCode(" ");});
            assertThrows(InvalidProductCodeException.class, () -> {ez.getProductTypeByBarCode("8005235078695");});


            ProductTypeImpl p = new ProductTypeImpl(1, "milk", "8711600786226", 3.50, "notes");
            ez.createProductType("milk", "8711600786226", 3.50, "notes");  //insert the previous product in the application

            //not exist any product with that barcode - return null
            assertNull(ez.getProductTypeByBarCode("8005235078697"));

            //exist a product with that barcode - return the product
            ProductType p1 =ez.getProductTypeByBarCode("8711600786226");
            assertNotNull(p1); //exist product with that barcode
            assertEquals(p.toString(), p1.toString()); //check if the product find is that we wanted

            //modify productType price per unit
            p1.setPricePerUnit(5.80);
            assertEquals(ez.getProductTypeByBarCode("8711600786226").toString(), p1.toString());



        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    public void testGetProductByDescription() {
        try {
            EZShop ez = new EZShop();
            EZShopDB db = new EZShopDB();

            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.getProductTypesByDescription("pizza");});

            //Unauthorized user
            ez.login("john", "5678"); //cashier
            assertThrows(UnauthorizedException.class, () -> {ez.getProductTypesByDescription("milk");});

            //authorized user
            ez.login("emma", "1234");



            ez.createProductType("pizza", "8004020983710", 5.50, "notes"); //create another product with different description that should not appear in the list returned from the method
            ProductTypeImpl p1 = new ProductTypeImpl(1, "pizza", "8004020983710", 5.50, "notes");

            //there is no product with "milk" description
            assertTrue(ez.getProductTypesByDescription("milk").isEmpty());

            List<ProductType> Prod_w_same_description = new ArrayList<>();

            ProductTypeImpl p2 = new ProductTypeImpl(2, "milk", "8711600786226", 3.50, "notes");
            ez.createProductType("milk", "8711600786226", 3.50, "notes");  //insert the previous product in the application
            Prod_w_same_description.add(p2);

            //there is 1 product with "milk" description
            assertEquals(1, ez.getProductTypesByDescription("milk").size());
            assertEquals(Prod_w_same_description.toString(), ez.getProductTypesByDescription("milk").toString());


            ez.createProductType("milk", "8005235078697", 6.60, "new tipology of milk"); //create another product with same description that should appear in the list returned from the method
            ProductTypeImpl p3 = new ProductTypeImpl(3, "milk", "8005235078697", 6.60, "new tipology of milk");
            Prod_w_same_description.add(p3);

            //there are 2 products with "milk" description
            assertEquals(2, ez.getProductTypesByDescription("milk").size());
            assertEquals(Prod_w_same_description.toString(), ez.getProductTypesByDescription("milk").toString());

        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    public void testUpdateProductQuantity() {
        try {
            EZShop ez = new EZShop();
            EZShopDB db = new EZShopDB();

            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.updateQuantity(1, 30);});

            //unauthorized user
            ez.login("john", "5678"); //cashier
            assertThrows(UnauthorizedException.class, () -> {ez.updateQuantity(1, 30);});

            //authorized user
            ez.login("emma", "1234");

            //EXCEPTION throw InvalidProductId
            assertThrows(InvalidProductIdException.class, () -> { ez.updateQuantity(-5, 26); });
            assertThrows(InvalidProductIdException.class, () -> { ez.updateQuantity(null, 45); });


            ez.createProductType("milk", "8005235078697", 3.70, "dairy products");
            ProductType p1 = ez.getProductTypeByBarCode("8005235078697");
            p1.setQuantity(20);

            //position null - not valid
            assertFalse(ez.updateQuantity(p1.getId(), 28));

            p1.setLocation("12-A-5");

            //Product Id not found - not valid
            assertFalse(ez.updateQuantity(3, 48));

            //total Amount <0 - not valid
            assertFalse(ez.updateQuantity(p1.getId(), -30));

            //valid
            assertTrue(ez.updateQuantity(p1.getId(), -5)); //with toBeAdded negative
            assertTrue(ez.updateQuantity(p1.getId(), 17)); //with toBeAdded positive

        } catch (InvalidProductIdException e) {
            e.printStackTrace();
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    public void testUpdateProductPosition() {
        try {
            EZShop ez = new EZShop();
            EZShopDB db= new EZShopDB();

            //no user logged
            assertThrows(UnauthorizedException.class, () -> {ez.updatePosition(1, "1-D-3");});

            //unauthorized user
            ez.login("john", "5678"); //cashier
            assertThrows(UnauthorizedException.class, () -> {ez.updatePosition(1, "1-D-3");});

            //authorized user
            ez.login("emma", "1234");

            //EXCEPTION thrown InvalidProductId
            assertThrows(InvalidProductIdException.class, () -> {ez.updatePosition(-5, "12-A-2"); });
            assertThrows(InvalidProductIdException.class, () -> {ez.updatePosition(null, "1-E-4"); });

            //EXCEPTION thrown InvalidLocation
            assertThrows(InvalidLocationException.class, () -> {ez.updatePosition(1, "1-E"); });
            assertThrows(InvalidLocationException.class, () -> {ez.updatePosition(1, "1-E1-4"); });
            assertThrows(InvalidLocationException.class, () -> {ez.updatePosition(1, "e1-E-4"); });
            assertThrows(InvalidLocationException.class, () -> {ez.updatePosition(1, "1-E-004"); });


            ez.createProductType("milk", "8711600786226", 3.50, "notes");
            ProductType p1 = ez.getProductTypeByBarCode("8711600786226");
            p1.setLocation("12-A-4");

            //if already exists a product in the position assigned in newPos - not valid
            assertFalse(ez.updatePosition(p1.getId(), "12-A-4"));

            //Id not found - not vailid
            assertFalse(ez.updatePosition(4, "13-B-4"));

            //update Product position from a valid one to another one - valid
            assertTrue(ez.updatePosition(p1.getId(), "13-B-4"));


            ez.createProductType("pizza", "8004020983710", 5.50, "notes"); //create another product with different description that should not appear in the list returned from the method
            ProductType p2 = ez.getProductTypeByBarCode("8004020983710");

            //update Product position from null to a valid one - valid
            assertTrue(ez.updatePosition(p2.getId(), "1-A-89"));

            //the new pos is the same of the previous but with lowercase letter - not valid, position already occupied
            assertFalse(ez.updatePosition(p1.getId(), "1-a-89"));

        } catch (InvalidProductIdException e) {
            e.printStackTrace();
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        } catch (InvalidProductDescriptionException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidPricePerUnitException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
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
