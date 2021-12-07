package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import org.junit.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class TestEZShopDB {


    /**
     * Before each test case resets the DB
     */
    @Before
    public void resetDB() {
        try {
            EZShopDB db = new EZShopDB();
            db.resetDB();

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testGetProductsEmpty() {
        try {
            EZShopDB db = new EZShopDB();
            assertEquals(0, db.getProducts().size());
        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetOneProductType() {
        try {
            EZShopDB db = new EZShopDB();
            ProductTypeImpl p = new ProductTypeImpl(1, "milk", "8005235078697", 1.50, "dairy products");

            db.insertProductTypeDB(p);

            List<ProductType> products = new ArrayList(db.getProducts());
            assertEquals(1, products.size());
            assertEquals(p.getId(), products.get(0).getId());
            assertEquals(p.getProductDescription(), products.get(0).getProductDescription());
            assertEquals(p.getBarCode(), products.get(0).getBarCode());
            assertEquals(p.getPricePerUnit(), products.get(0).getPricePerUnit(), 0.0001);
            assertEquals(p.getPricePerUnit(), products.get(0).getPricePerUnit());
            assertEquals(p.getNote(), products.get(0).getNote());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetTwoProductType() {
        try {
            EZShopDB db = new EZShopDB();
            ProductTypeImpl p1 = new ProductTypeImpl(1, "milk", "8005235078697", 1.50, "dairy products");
            ProductTypeImpl p2 = new ProductTypeImpl(2, "pizza", "800402093710", 5.00, "mozzarella cheese and tomatoes");
            db.insertProductTypeDB(p1);
            db.insertProductTypeDB(p2);

            List<ProductType> products = new ArrayList(db.getProducts());
            assertEquals(2, products.size());

            assertEquals(p1.getId(), products.get(0).getId());
            assertEquals(p1.getProductDescription(), products.get(0).getProductDescription());
            assertEquals(p1.getBarCode(), products.get(0).getBarCode());
            assertEquals(p1.getPricePerUnit(), products.get(0).getPricePerUnit(), 0.0001);
            assertEquals(p1.getNote(), products.get(0).getNote());

            assertEquals(p2.getId(), products.get(1).getId());
            assertEquals(p2.getProductDescription(), products.get(1).getProductDescription());
            assertEquals(p2.getBarCode(), products.get(1).getBarCode());
            assertEquals(p2.getPricePerUnit(), products.get(1).getPricePerUnit(), 0.0001);
            assertEquals(p2.getNote(), products.get(1).getNote());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateProductType() {
        try {
            EZShopDB db = new EZShopDB();
            ProductTypeImpl p = new ProductTypeImpl(1, "milk", "8005235078697", 1.50, "dairy products");

            db.insertProductTypeDB(p);
            p.setProductDescription("pizza");
            p.setPricePerUnit(5.00);
            p.setNote("mozzarella cheese and tomatoes");
            db.updateProductTypeDB(p);

            List<ProductType> products = new ArrayList(db.getProducts());
            assertEquals(1, products.size());
            assertEquals(p.getProductDescription(), products.get(0).getProductDescription());
            assertEquals(p.getPricePerUnit(), products.get(0).getPricePerUnit(), 0.0001);
            assertEquals(p.getNote(), products.get(0).getNote());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }


    @Test
    public void testDeleteProductType() {
        try {
            EZShopDB db = new EZShopDB();
            ProductTypeImpl p = new ProductTypeImpl(1, "milk", "8005235078697", 1.50, "dairy products");

            db.insertProductTypeDB(p);

            db.deleteProductTypeDB(p.getId());
            assertEquals(0, db.getProducts().size());


        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateQuantityProd() {
        try {
            EZShopDB db = new EZShopDB();
            ProductTypeImpl p = new ProductTypeImpl(1, "milk", "8005235078697", 1.50, "dairy products");
            p.setQuantity(46);

            db.insertProductTypeDB(p);

            int new_quantity = 25;

            db.updateQuantityDB(p.getId(), new_quantity);
            p.setQuantity(new_quantity);

            List<ProductType> products = new ArrayList(db.getProducts());
            assertEquals(1, products.size());
            assertEquals(p.getQuantity(), products.get(0).getQuantity());


        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdatePositionProd() {
        try {
            EZShopDB db = new EZShopDB();
            ProductTypeImpl p = new ProductTypeImpl(1, "milk", "8005235078697", 1.50, "dairy products");
            p.setLocation("12-S-7");

            db.insertProductTypeDB(p);

            String new_location = "1-A-3";

            db.updatePositionDB(p.getId(), new_location);

            List<ProductType> products = new ArrayList(db.getProducts());
            assertEquals(1, products.size());
            assertEquals(new_location, products.get(0).getLocation());


        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetTicketEntries() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry te1 = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            TicketEntry te2 = new TicketEntryImpl("8001480034860", "Pencil",
                    50, 0.30, 0.0);
            List<TicketEntry> listTest = null;

            db.insertTicketEntry(1, te1);
            db.insertTicketEntry(1, te2);

            listTest = db.getTicketEntries(1);
            assertNotNull(listTest);
            assertNotNull(listTest.get(0));
            assertNotNull(listTest.get(1));

            assertEquals(te1.getBarCode(), listTest.get(0).getBarCode());
            assertEquals(te1.getProductDescription(), listTest.get(0).getProductDescription());
            assertEquals(te1.getAmount(), listTest.get(0).getAmount());
            assertEquals(te1.getPricePerUnit(), listTest.get(0).getPricePerUnit(), 0.00001);
            assertEquals(te1.getDiscountRate(), listTest.get(0).getDiscountRate(), 0.00001);

            assertEquals(te2.getBarCode(), listTest.get(1).getBarCode());
            assertEquals(te2.getProductDescription(), listTest.get(1).getProductDescription());
            assertEquals(te2.getAmount(), listTest.get(1).getAmount());
            assertEquals(te2.getPricePerUnit(), listTest.get(1).getPricePerUnit(), 0.00001);
            assertEquals(te2.getDiscountRate(), listTest.get(1).getDiscountRate(), 0.00001);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testGetSaleTransactions() {
        try {
            EZShopDB db = new EZShopDB();
            TreeMap<Integer, SaleTransaction> saletransactions = new TreeMap<>();
            SaleTransactionImpl st1 = new SaleTransactionImpl(1);
            SaleTransactionImpl st2 = new SaleTransactionImpl(2);
            List<TicketEntry> list1 = new ArrayList<>();
            List<TicketEntry> list2 = new ArrayList<>();
            TicketEntry te1 = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            list1.add(te1);
            st1.setEntries(list1);
            TicketEntry te2 = new TicketEntryImpl("8001480034860", "Pencil",
                    50, 0.30, 0.0);
            list2.add(te2);
            st2.setEntries(list2);

            db.insertTicketEntry(1, te1);
            db.insertTicketEntry(2, te2);
            db.insertSaleTransaction(st1);
            db.insertSaleTransaction(st2);
            saletransactions = db.getSaleTransactions();

            assertEquals(2, saletransactions.size());
            assertEquals(1, saletransactions.get(1).getEntries().size());
            assertEquals(1, saletransactions.get(2).getEntries().size());

            db.closeConnection();
        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetSaleTransaction() {
        try {
            EZShopDB db = new EZShopDB();
            SaleTransactionImpl st = new SaleTransactionImpl(1);
            SaleTransactionImpl stTest;
            List<TicketEntry> list1 = new ArrayList<>();
            TicketEntry te1 = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            list1.add(te1);
            TicketEntry te2 = new TicketEntryImpl("8001480034860", "Pencil",
                    50, 0.30, 0.0);
            list1.add(te2);
            st.setEntries(list1);
            st.setBalanceId(1);
            st.setPrice(100.00);
            st.setDiscountRate(0.20);
            st.setState("CLOSED");

            db.insertSaleTransaction(st);
            db.insertTicketEntry(st.getTicketNumber(), te1);
            db.insertTicketEntry(st.getTicketNumber(), te2);

            //TreeMap<Integer, SaleTransaction> mappa = db.getSaleTransactions();
            stTest = (SaleTransactionImpl) db.getSaleTransaction(1);
            //stTest = (SaleTransactionImpl) mappa.get(1);
            assertEquals(new Integer(1), stTest.getTicketNumber());
            assertEquals(new Integer(1), stTest.getBalanceId());
            assertEquals(100.00, stTest.getPrice(), 0.00001);
            assertEquals(0.20, stTest.getDiscountRate(), 0.00001);
            assertEquals("CLOSED", stTest.getState());
            assertEquals(2, stTest.getEntries().size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertSaleTransactionThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            SaleTransactionImpl st = new SaleTransactionImpl(1);
            TicketEntry te = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            List<TicketEntry> list1 = new ArrayList<>();
            st.setEntries(list1);
            db.insertSaleTransaction(st);
            //Cannot insert a SaleTransaction if its id is the same of another recorded SaleTransaction in DB
            assertThrows(SQLException.class, () -> {
                db.insertSaleTransaction(st);
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testGetSaleTransactionNoRecordFound() {
        try {
            EZShopDB db = new EZShopDB();
            SaleTransactionImpl st = new SaleTransactionImpl(1);
            TicketEntry te = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            List<TicketEntry> list1 = new ArrayList<>();
            st.setEntries(list1);
            db.insertSaleTransaction(st);
            //Cannot get a SaleTransaction if its id doesn't exist in DB
            assertNull(db.getSaleTransaction(2));

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateSaleTransaction() {
        try {
            EZShopDB db = new EZShopDB();
            SaleTransactionImpl st = new SaleTransactionImpl(1);
            SaleTransactionImpl stUpdate = new SaleTransactionImpl(1);
            SaleTransactionImpl stTest;
            List<TicketEntry> list1 = new ArrayList<>();
            TicketEntry te1 = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            list1.add(te1);
            TicketEntry te2 = new TicketEntryImpl("8001480034860", "Pencil",
                    50, 0.30, 0.0);
            list1.add(te2);
            st.setEntries(list1);
            st.setBalanceId(1);
            st.setState("PAYED");

            db.insertSaleTransaction(st);
            db.insertTicketEntry(st.getTicketNumber(), te1);
            db.insertTicketEntry(st.getTicketNumber(), te2);

            stUpdate.setEntries(list1);
            stUpdate.setBalanceId(2);
            stUpdate.setPrice(100.00);
            stUpdate.setDiscountRate(0.20);
            stUpdate.setState("CLOSED");

            db.updateSaleTransaction(stUpdate);

            stTest = (SaleTransactionImpl) db.getSaleTransaction(1);
            assertNotNull(stTest);
            assertEquals(new Integer(1), stTest.getTicketNumber());
            assertEquals(new Integer(2), stTest.getBalanceId());
            assertEquals(100.00, stTest.getPrice(), 0.00001);
            assertEquals(0.20, stTest.getDiscountRate(), 0.00001);
            assertEquals("CLOSED", stTest.getState());


        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteSaleTransaction() {
        try {
            EZShopDB db = new EZShopDB();
            SaleTransactionImpl st1 = new SaleTransactionImpl(1);
            SaleTransactionImpl st2 = new SaleTransactionImpl(2);
            List<TicketEntry> list1 = new ArrayList<>();
            TicketEntry te1 = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            list1.add(te1);
            TicketEntry te2 = new TicketEntryImpl("8001480034860", "Pencil",
                    50, 0.30, 0.0);
            st1.setState("CLOSED");
            st2.setState("CLOSED");

            db.insertSaleTransaction(st1);
            db.insertTicketEntry(st1.getTicketNumber(), te1);
            db.insertTicketEntry(st1.getTicketNumber(), te2);
            db.insertSaleTransaction(st2);
            db.insertTicketEntry(st2.getTicketNumber(), te1);
            db.insertTicketEntry(st2.getTicketNumber(), te2);

            assertEquals(2, db.getSaleTransactions().size());

            assertTrue(db.deleteSaleTransaction(1));
            assertEquals(1, db.getSaleTransactions().size());
            assertEquals(0, db.getTicketEntries(1).size());

            db.deleteSaleTransaction(2);
            assertEquals(0, db.getSaleTransactions().size());
            assertEquals(0, db.getTicketEntries(2).size());

            db.closeConnection();
        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteAllReturnedProducts() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t1 = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            TicketEntry t2 = new TicketEntryImpl("8001480034860", "Pencil",
                    50, 0.30, 0.0);

            db.insertReturnedProduct(1, t1);
            db.insertReturnedProduct(2, t2);

            db.deleteAllReturnedProducts();
            assertEquals(0, db.getReturnedProducts(1).size());
            assertEquals(0, db.getReturnedProducts(2).size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteReturnedProduct() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);

            db.insertReturnedProduct(1, t);

            db.deleteReturnedProduct(1, "6973205090043");
            assertEquals(0, db.getReturnedProducts(1).size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteReturnedProductThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            assertThrows(SQLException.class, () -> {
                db.deleteReturnedProduct(1, t.getBarCode());
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteReturnedProductsInReturn() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t1 = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            TicketEntry t2 = new TicketEntryImpl("8001480034860", "Pencil",
                    50, 0.30, 0.0);

            db.insertReturnedProduct(1, t1);
            db.insertReturnedProduct(1, t2);

            db.deleteReturnedProductsInReturn(1);
            assertEquals(0, db.getReturnedProducts(1).size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetOneReturnedProduct() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);

            db.insertReturnedProduct(1, t);

            List<TicketEntry> returnedProducts = db.getReturnedProducts(1);
            assertEquals(1, returnedProducts.size());
            assertEquals(t.getBarCode(), returnedProducts.get(0).getBarCode());
            assertEquals(t.getProductDescription(), returnedProducts.get(0).getProductDescription());
            assertEquals(t.getAmount(), returnedProducts.get(0).getAmount());
            assertEquals(t.getPricePerUnit(), returnedProducts.get(0).getPricePerUnit(), 0.00001);
            assertEquals(t.getDiscountRate(), returnedProducts.get(0).getDiscountRate(), 0.00001);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertReturnedProductThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);

            db.insertReturnedProduct(1, t);

            assertThrows(SQLException.class, () -> {
                db.insertReturnedProduct(1, t);
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetTwoReturnedProducts() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t1 = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            TicketEntry t2 = new TicketEntryImpl("8001480034860", "Pencil",
                    50, 0.30, 0.0);

            db.insertReturnedProduct(1, t1);
            db.insertReturnedProduct(1, t2);

            List<TicketEntry> returnedProducts = db.getReturnedProducts(1);
            assertEquals(2, returnedProducts.size());
            assertEquals(t1.getBarCode(), returnedProducts.get(0).getBarCode());
            assertEquals(t1.getProductDescription(), returnedProducts.get(0).getProductDescription());
            assertEquals(t1.getAmount(), returnedProducts.get(0).getAmount());
            assertEquals(t1.getPricePerUnit(), returnedProducts.get(0).getPricePerUnit(), 0.00001);
            assertEquals(t1.getDiscountRate(), returnedProducts.get(0).getDiscountRate(), 0.00001);

            assertEquals(t2.getBarCode(), returnedProducts.get(1).getBarCode());
            assertEquals(t2.getProductDescription(), returnedProducts.get(1).getProductDescription());
            assertEquals(t2.getAmount(), returnedProducts.get(1).getAmount());
            assertEquals(t2.getPricePerUnit(), returnedProducts.get(1).getPricePerUnit(), 0.00001);
            assertEquals(t2.getDiscountRate(), returnedProducts.get(1).getDiscountRate(), 0.00001);

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testGetReturnedProductsEmpty() {
        try {
            EZShopDB db = new EZShopDB();
            assertEquals(0, db.getReturnedProducts(1).size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateReturnedProduct() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);

            db.insertReturnedProduct(1, t);
            t.setAmount(30);
            db.updateReturnedProduct(1, t);

            List<TicketEntry> returnedProducts = db.getReturnedProducts(1);
            assertEquals(t.getAmount(), returnedProducts.get(0).getAmount());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateReturnedProductThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            TicketEntry t = new TicketEntryImpl("6973205090043", "Notebook",
                    20, 1.50, 0.0);
            assertThrows(SQLException.class, () -> {
                db.updateReturnedProduct(1, t);
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteReturnTransactions() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt1 = new ReturnTransaction(1, 1);
            ReturnTransaction rt2 = new ReturnTransaction(2, 2);

            db.insertReturnTransaction(rt1);
            db.insertReturnTransaction(rt2);

            db.deleteReturnTransactions();
            assertEquals(0, db.getReturnTransactions().size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteReturnTransaction() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt = new ReturnTransaction(1, 1);

            db.insertReturnTransaction(rt);

            db.deleteReturnTransaction(rt.getId());
            assertEquals(0, db.getReturnTransactions().size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteReturnTransactionThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt = new ReturnTransaction(1, 1);
            assertThrows(SQLException.class, () -> {
                db.deleteReturnTransaction(rt.getId());
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetOneReturnTransaction() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt = new ReturnTransaction(1, 1);

            db.insertReturnTransaction(rt);

            List<ReturnTransaction> returnTransactions = new ArrayList<>(db.getReturnTransactions().values());
            assertEquals(1, returnTransactions.size());
            assertEquals(rt.getId(), returnTransactions.get(0).getId());
            assertEquals(rt.getSaleTransactionId(), returnTransactions.get(0).getSaleTransactionId());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertReturnTransactionThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt = new ReturnTransaction(1, 1);

            db.insertReturnTransaction(rt);

            assertThrows(SQLException.class, () -> {
                db.insertReturnTransaction(rt);
            });
        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetTwoReturnTransactions() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt1 = new ReturnTransaction(1, 1);
            ReturnTransaction rt2 = new ReturnTransaction(2, 2);

            db.insertReturnTransaction(rt1);
            db.insertReturnTransaction(rt2);

            List<ReturnTransaction> returnTransactions = new ArrayList<>(db.getReturnTransactions().values());
            assertEquals(2, returnTransactions.size());
            assertEquals(rt1.getId(), returnTransactions.get(0).getId());
            assertEquals(rt1.getSaleTransactionId(), returnTransactions.get(0).getSaleTransactionId());
            assertEquals(rt2.getId(), returnTransactions.get(1).getId());
            assertEquals(rt2.getSaleTransactionId(), returnTransactions.get(1).getSaleTransactionId());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testGetReturnTransactionsEmpty() {
        try {
            EZShopDB db = new EZShopDB();

            List<ReturnTransaction> returnTransactions = new ArrayList<>(db.getReturnTransactions().values());
            assertEquals(0, returnTransactions.size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateReturnTransactionClosed() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt = new ReturnTransaction(1, 1);

            db.insertReturnTransaction(rt);
            rt.setState("CLOSED");
            db.updateReturnTransaction(rt);

            List<ReturnTransaction> returnTransactions = new ArrayList<>(db.getReturnTransactions().values());
            assertEquals(rt.getState(), returnTransactions.get(0).getState());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateReturnTransactionPayed() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt = new ReturnTransaction(1, 1);

            db.insertReturnTransaction(rt);
            rt.setState("PAYED");
            rt.setBalanceId(1);
            db.updateReturnTransaction(rt);

            List<ReturnTransaction> returnTransactions = new ArrayList<>(db.getReturnTransactions().values());
            assertEquals(rt.getState(), returnTransactions.get(0).getState());
            assertEquals(rt.getBalanceId(), returnTransactions.get(0).getBalanceId());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateReturnTransactionThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            ReturnTransaction rt = new ReturnTransaction(1, 1);
            assertThrows(SQLException.class, () -> {
                db.updateReturnTransaction(rt);
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteBalanceOperations() {
        try {
            EZShopDB db = new EZShopDB();
            BalanceOperation b1 = new BalanceOperationImpl(1,
                    LocalDate.of(2021, 5, 26), 150.0, "DEBIT");
            BalanceOperation b2 = new BalanceOperationImpl(2,
                    LocalDate.of(2021, 5, 30), 300.0, "CREDIT");

            db.insertBalanceOperation(b1);
            db.insertBalanceOperation(b2);

            db.deleteBalanceOperations();
            assertEquals(0, db.getBalanceOperations().size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetOneBalanceOperation() {
        try {
            EZShopDB db = new EZShopDB();
            BalanceOperation b = new BalanceOperationImpl(1,
                    LocalDate.of(2021, 5, 26), 150.0, "DEBIT");

            db.insertBalanceOperation(b);

            List<BalanceOperation> balanceOperations = new ArrayList<>(db.getBalanceOperations().values());
            assertEquals(1, balanceOperations.size());
            assertEquals(b.getBalanceId(), balanceOperations.get(0).getBalanceId());
            assertEquals(b.getDate(), balanceOperations.get(0).getDate());
            assertEquals(b.getMoney(), balanceOperations.get(0).getMoney(), 0.00001);
            assertEquals(b.getType(), balanceOperations.get(0).getType());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertBalanceOperationThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            BalanceOperation b = new BalanceOperationImpl(1,
                    LocalDate.of(2021, 5, 26), 150.0, "DEBIT");

            db.insertBalanceOperation(b);

            assertThrows(SQLException.class, () -> {
                db.insertBalanceOperation(b);
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetTwoBalanceOperations() {
        try {
            EZShopDB db = new EZShopDB();
            BalanceOperation b1 = new BalanceOperationImpl(1,
                    LocalDate.of(2021, 5, 26), 150.0, "DEBIT");
            BalanceOperation b2 = new BalanceOperationImpl(2,
                    LocalDate.of(2021, 5, 30), 300.0, "CREDIT");

            db.insertBalanceOperation(b1);
            db.insertBalanceOperation(b2);

            List<BalanceOperation> balanceOperations = new ArrayList<>(db.getBalanceOperations().values());
            assertEquals(2, balanceOperations.size());
            assertEquals(b1.getBalanceId(), balanceOperations.get(0).getBalanceId());
            assertEquals(b1.getDate(), balanceOperations.get(0).getDate());
            assertEquals(b1.getMoney(), balanceOperations.get(0).getMoney(), 0.00001);
            assertEquals(b1.getType(), balanceOperations.get(0).getType());
            assertEquals(b2.getBalanceId(), balanceOperations.get(1).getBalanceId());
            assertEquals(b2.getDate(), balanceOperations.get(1).getDate());
            assertEquals(b2.getMoney(), balanceOperations.get(1).getMoney(), 0.00001);
            assertEquals(b2.getType(), balanceOperations.get(1).getType());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testGetBalanceOperationsEmpty() {
        try {
            EZShopDB db = new EZShopDB();
            assertEquals(0, db.getBalanceOperations().size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testDeleteOrders() {
        try {
            EZShopDB db = new EZShopDB();
            Order o1 = new OrderImpl(1, "6973205090043", 0.50, 20);
            Order o2 = new OrderImpl(2, "8001480034860", 5.00, 100);

            db.insertOrder(o1);
            db.insertOrder(o2);

            db.deleteOrders();
            assertEquals(0, db.getOrders().size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetOneOrder() {
        try {
            EZShopDB db = new EZShopDB();
            Order o = new OrderImpl(1, "6973205090043", 0.50, 20);

            db.insertOrder(o);

            List<Order> orders = new ArrayList<>(db.getOrders().values());
            assertEquals(1, orders.size());
            assertEquals(o.getOrderId(), orders.get(0).getOrderId());
            assertEquals(o.getProductCode(), orders.get(0).getProductCode());
            assertEquals(o.getPricePerUnit(), orders.get(0).getPricePerUnit(), 0.00001);
            assertEquals(o.getQuantity(), orders.get(0).getQuantity());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertOrderThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            Order o = new OrderImpl(1, "6973205090043", 0.50, 20);

            db.insertOrder(o);

            assertThrows(SQLException.class, () -> {
                db.insertOrder(o);
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetTwoOrders() {
        try {
            EZShopDB db = new EZShopDB();
            Order o1 = new OrderImpl(1, "6973205090043", 0.50, 20);
            Order o2 = new OrderImpl(2, "8001480034860", 5.00, 100);

            db.insertOrder(o1);
            db.insertOrder(o2);

            List<Order> orders = new ArrayList<>(db.getOrders().values());
            assertEquals(2, orders.size());
            assertEquals(o1.getOrderId(), orders.get(0).getOrderId());
            assertEquals(o1.getProductCode(), orders.get(0).getProductCode());
            assertEquals(o1.getPricePerUnit(), orders.get(0).getPricePerUnit(), 0.00001);
            assertEquals(o1.getQuantity(), orders.get(0).getQuantity());

            assertEquals(o2.getOrderId(), orders.get(1).getOrderId());
            assertEquals(o2.getProductCode(), orders.get(1).getProductCode());
            assertEquals(o2.getPricePerUnit(), orders.get(1).getPricePerUnit(), 0.00001);
            assertEquals(o2.getQuantity(), orders.get(1).getQuantity());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testGetOrdersEmpty() {
        try {
            EZShopDB db = new EZShopDB();
            assertEquals(0, db.getOrders().size());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateOrder() {
        try {
            EZShopDB db = new EZShopDB();
            Order o = new OrderImpl(1, "6973205090043", 0.50, 20);

            db.insertOrder(o);
            o.setStatus("PAYED");
            o.setBalanceId(2);
            db.updateOrder(o);

            List<Order> orders = new ArrayList<>(db.getOrders().values());
            assertEquals(o.getStatus(), orders.get(0).getStatus());
            assertEquals(o.getBalanceId(), orders.get(0).getBalanceId());

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testUpdateOrderThrowsException() {
        try {
            EZShopDB db = new EZShopDB();
            Order o = new OrderImpl(1, "6973205090043", 0.50, 20);
            assertThrows(SQLException.class, () -> {
                db.updateOrder(o);
            });

        } catch (SQLException throwables) {
            fail(throwables.getMessage());
        }
    }

    @Test
    public void testInsertAndGetUsers() {
        try {
            EZShopDB db = new EZShopDB();
            assertEquals(0, db.getAllUser().size());

            User u1 = new UserImpl(1, "user1", "password", "Administrator");
            User u2 = new UserImpl(2, "user2", "password", "Cashier");

            assertTrue(db.insertUser(u1));
            assertEquals(1, db.getAllUser().size());
            assertThrows(SQLException.class, () -> {
                db.insertUser(u1);
            });

            assertTrue(db.insertUser(u2));
            assertEquals(2, db.getAllUser().size());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteUser() {
        try {
            EZShopDB db = new EZShopDB();
            User u1 = new UserImpl(1, "user1", "password", "Administrator");
            User u2 = new UserImpl(2, "user2", "password", "Cashier");

            assertTrue(db.insertUser(u1));
            assertEquals(1, db.getAllUser().size());

            assertTrue(db.insertUser(u2));
            assertEquals(2, db.getAllUser().size());

            assertTrue(db.deleteUser(1));
            assertEquals(1, db.getAllUser().size());

            assertTrue(db.deleteUser(2));
            assertEquals(0, db.getAllUser().size());

            assertFalse(db.deleteUser(2));

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void updateUser() {
        try {
            EZShopDB db = new EZShopDB();
            User u1 = new UserImpl(1, "user1", "password", "Administrator");

            assertTrue(db.insertUser(u1));
            assertTrue(db.updateUserRight(u1.getId(), "ShopManager"));
            assertEquals("ShopManager", db.getUser(u1.getId()).getRole());

            assertFalse(db.updateUserRight(2, "Cashier"));

           // db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testInsertAndGetCustomer() {
        try {
            EZShopDB db = new EZShopDB();
            assertEquals(0, db.getAllCustomer().size());

            Customer c1 = new CustomerImpl(1, "customer1");
            Customer c2 = new CustomerImpl(2, "customer2");

            assertTrue(db.insertCustomer(c1));
            assertEquals(1, db.getAllCustomer().size());
            assertThrows(SQLException.class, () -> {
                db.insertCustomer(c1);
            });
            assertTrue(db.insertCustomer(c2));
            assertEquals(2, db.getAllCustomer().size());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteCustomer() {
        try {
            EZShopDB db = new EZShopDB();
            Customer c1 = new CustomerImpl(1, "customer1");
            Customer c2 = new CustomerImpl(2, "customer2");

            assertTrue(db.insertCustomer(c1));
            assertTrue(db.insertCustomer(c2));

            assertTrue(db.deleteCustomer(c1.getId()));
            assertEquals(1, db.getAllCustomer().size());

            assertTrue(db.deleteCustomer(c2.getId()));
            assertEquals(0, db.getAllCustomer().size());

            assertFalse(db.deleteCustomer(c2.getId()));

            ///db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateCustomerPoints() {
        try {
            EZShopDB db = new EZShopDB();
            Customer c1 = new CustomerImpl(1, "customer1");

            assertFalse(db.updateCustomerPoints(c1.getId(), 10));
            assertTrue(db.insertCustomer(c1));
            assertTrue(db.updateCustomerPoints(c1.getId(), 10));
            assertEquals((Integer) 10, db.getCustomer(c1.getId()).getPoints());
            assertTrue(db.updateCustomerPoints(c1.getId(), -5));
            assertEquals((Integer) 5, db.getCustomer(c1.getId()).getPoints());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateCustomerName() {
        try {
            EZShopDB db = new EZShopDB();
            Customer c1 = new CustomerImpl(1, "customer1");

            assertFalse(db.updateCustomerName(c1.getId(), "Pippo"));
            assertTrue(db.insertCustomer(c1));
            assertTrue(db.updateCustomerName(c1.getId(), "Pippo"));
            assertEquals("Pippo", db.getCustomer(c1.getId()).getCustomerName());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateCustomerCard() {
        try {
            EZShopDB db = new EZShopDB();
            Customer c1 = new CustomerImpl(1, "customer1");

            assertFalse(db.updateCustomerCard(c1.getId(), "1234567890"));
            assertTrue(db.insertCustomer(c1));
            assertTrue(db.updateCustomerCard(c1.getId(), "1234567890"));
            assertEquals("1234567890", db.getCustomer(c1.getId()).getCustomerCard());
            assertTrue(db.updateCustomerCard(c1.getId(), null));
            assertNull(db.getCustomer(c1.getId()).getCustomerCard());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateCustomer() {
        try {
            EZShopDB db = new EZShopDB();
            Customer c1 = new CustomerImpl(1, "customer1");

            assertFalse(db.updateCustomer(c1.getId(), "Pippo", "1234567890"));
            assertTrue(db.insertCustomer(c1));
            assertTrue(db.updateCustomer(c1.getId(), "Pippo", "1234567890"));

            Customer c = db.getCustomer(c1.getId());
            assertEquals("1234567890", c.getCustomerCard());
            assertEquals("Pippo", c.getCustomerName());

            assertTrue(db.updateCustomer(c1.getId(), "Pippo", null));
            c = db.getCustomer(c1.getId());
            assertEquals("Pippo", c.getCustomerName());
            assertNull(c.getCustomerCard());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testInsertAndGetLoyaltyCard() {
        try {
            EZShopDB db = new EZShopDB();
            LoyaltyCardImpl card1 = new LoyaltyCardImpl("1111111111", true);
            LoyaltyCardImpl card2 = new LoyaltyCardImpl("2222222222", true);
            LoyaltyCardImpl card3 = new LoyaltyCardImpl("3333333333", false);
            assertNull(db.getAssignableLoyaltyCardCode());
            assertTrue(db.insertLoyaltyCard(card1));
            assertTrue(db.insertLoyaltyCard(card2));
            assertNull(db.getAssignableLoyaltyCardCode());
            assertThrows(SQLException.class, () -> {
                db.insertLoyaltyCard(card1);
            });
            assertTrue(db.insertLoyaltyCard(card3));
            assertEquals("3333333333", db.getAssignableLoyaltyCardCode());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateCardAssigned() {
        try {
            EZShopDB db = new EZShopDB();
            LoyaltyCardImpl card1 = new LoyaltyCardImpl("1111111111", true);
            LoyaltyCardImpl card2 = new LoyaltyCardImpl("2222222222", false);
            db.updateCardAssigned(card2.getCode(), true);
            db.insertLoyaltyCard(card1);
            db.insertLoyaltyCard(card2);
            db.updateCardAssigned(card2.getCode(), true);
            db.updateCardAssigned(card1.getCode(), false);
            assertEquals(card1.getCode(), db.getAssignableLoyaltyCardCode());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetLastCard() {
        try {
            EZShopDB db = new EZShopDB();

            LoyaltyCardImpl card1 = new LoyaltyCardImpl("0000000001", true);
            LoyaltyCardImpl card2 = new LoyaltyCardImpl("0000000002", false);
            LoyaltyCardImpl card3 = new LoyaltyCardImpl("0000000003", false);

            assertNull(db.getLastCard());
            db.insertLoyaltyCard(card1);
            assertEquals("0000000001", db.getLastCard());
            db.insertLoyaltyCard(card3);
            db.insertLoyaltyCard(card2);
            assertEquals("0000000003", db.getLastCard());
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testInsertGetProduct() {
        try {
            EZShopDB db = new EZShopDB();
            assertEquals(0, db.getAllProduct().size());

            Product p1 = new Product("0000001001", "8000825830419");
            Product p2 = new Product("0000001002", "8000825830419");

            assertTrue(db.insertProduct(p1));
            assertEquals(1, db.getAllProduct().size());
            assertEquals("0000001001", db.getProduct(p1.getRfid()).getRfid());
            assertNull(db.getProduct(p2.getRfid()));
            assertThrows(SQLException.class, () -> {
                db.insertProduct(p1);
            });
            assertTrue(db.insertProduct(p2));
            assertEquals(2, db.getAllProduct().size());
            assertEquals("0000001002", db.getProduct(p2.getRfid()).getRfid());

            //db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeleteProduct() {
        try {
            EZShopDB db = new EZShopDB();
            Product p1 = new Product("0000001001", "8000825830419");
            Product p2 = new Product("0000001002", "8000825830419");

            assertTrue(db.insertProduct(p1));
            assertTrue(db.insertProduct(p2));

            assertTrue(db.deleteProduct(p1.getRfid()));
            assertEquals(1, db.getAllProduct().size());

            assertTrue(db.deleteProduct(p2.getRfid()));
            assertEquals(0, db.getAllProduct().size());

            assertFalse(db.deleteProduct(p2.getRfid()));

            ///db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeleteAllProduct() {
        try {
            EZShopDB db = new EZShopDB();
            Product p1 = new Product("0000001001", "8000825830419");
            Product p2 = new Product("0000001002", "8000825830419");

            assertTrue(db.insertProduct(p1));
            assertTrue(db.insertProduct(p2));
            db.deleteAllProducts();
            assertEquals(0, db.getAllProduct().size());

            ///db.closeConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateProduct() {
        try {
            EZShopDB db = new EZShopDB();
            Product p1 = new Product("0000001001", "8000825830419");

            assertTrue(db.insertProduct(p1));
            assertTrue(db.updateProductSaleId(p1.getRfid(), 10));
            assertEquals(10, db.getProduct(p1.getRfid()).getSaleId());

            //db.closeConnection();
        } catch (SQLException e) {
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

