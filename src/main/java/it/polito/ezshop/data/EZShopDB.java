package it.polito.ezshop.data;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class EZShopDB {

    private String jdbcUrl = "jdbc:sqlite:EZShop.db";;
    private Connection connection;


    public EZShopDB() throws SQLException {
//        this.jdbcUrl = "jdbc:sqlite:EZShop.db";
//        this.connection = DriverManager.getConnection(jdbcUrl);
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

    public void openConnection() throws SQLException{
        this.connection = DriverManager.getConnection(jdbcUrl);
    }

    public void resetDB() throws SQLException {
        /*Statement statement = connection.createStatement();
        boolean isSuccess = true;

        List<String> tables = new ArrayList<>(Arrays.asList("ReturnTransaction",
                "SaleTransaction",
                "BalanceOperation",
                "Orders",
                "ProductType",
                "TicketEntry",
                "ReturnedProduct"));

        String sql = "";

        for(String t : tables){
            sql = "DELETE FROM " + t;
            if(statement.executeUpdate(sql) <= 0) {
                isSuccess = false;
                break;
            }
        }

        return isSuccess;*/
        deleteUsers();
        deleteCustomers();
        deleteProducts();
        deleteAllProducts();
        deleteOrders();
        deleteBalanceOperations();
        deleteAllReturnedProducts();
        deleteReturnTransactions();
        deleteSaleTransactions();
        deleteTicketEntries();
        deleteLoyaltyCards();
    }

    // PRODUCT TYPE DB
    public void deleteProducts() throws SQLException {
        openConnection();
        String sql = "DELETE FROM ProductType";
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }


    public ArrayList<ProductType> getProducts () throws SQLException {
        openConnection();
        ArrayList<ProductType> products= new ArrayList<>();

        String sql = "select * from ProductType";
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while(resultSet.next()) {
            int id = resultSet.getInt("id");
            String description = resultSet.getString("description");
            String barCode = resultSet.getString("barcode");
            double sellPrice = resultSet.getDouble("sellPrice");
            Integer quantity = resultSet.getInt("quantity");
            String notes = resultSet.getString("notes");
            String location = resultSet.getString("location");

            ProductType p= new ProductTypeImpl(id, description, barCode, sellPrice, notes);
            p.setQuantity(quantity);
            p.setLocation(location);
            products.add(p);
        }
        closeConnection();
        return products;
    }

    public boolean insertProductTypeDB(ProductTypeImpl prod) throws SQLException {
        openConnection();
        String sql = "insert into ProductType values("+ prod.getId()+ ",\'"+prod.getBarCode()+"\', \'"+prod.getProductDescription()+"\', "+ prod.getPricePerUnit()+", 0 , \'"+prod.getNote()+"\', null)";
        Statement statement = connection.createStatement();

        int insert = statement.executeUpdate(sql);
        closeConnection();
        if(insert >0)
            return true;

        return false;
    }

    public boolean updateProductTypeDB(ProductType prod) throws SQLException {
        openConnection();
        String sql = "update ProductType " +
                "set description ='" + prod.getProductDescription() + "' , barcode= '" + prod.getBarCode() + "', sellPrice= " + prod.getPricePerUnit() + ", notes= '" + prod.getNote()+"'"
                + "where id=" + prod.getId() ;

        Statement statement = connection.createStatement();

        int update = statement.executeUpdate(sql);
        closeConnection();
        if(update >0)
            return true;

        return false;
    }

    public boolean deleteProductTypeDB(Integer id) throws SQLException {
        openConnection();
        String sql = "delete from ProductType where id=" +id;
        Statement statement = connection.createStatement();

        int delete = statement.executeUpdate(sql);
        closeConnection();
        if(delete >0)
            return true;

        return false;
    }

    public boolean updateQuantityDB(int id, int newquantity) throws SQLException{
        openConnection();
        String sql = "update ProductType " +
                "set quantity= " + newquantity +
                " where id=" + id ;

        Statement statement = connection.createStatement();

        int updateq = statement.executeUpdate(sql);
        closeConnection();
        if(updateq >0)
            return true;

        return false;
    }

    public boolean updatePositionDB(Integer id, String newPos) throws SQLException{
        openConnection();
        String sql = "update ProductType " +
                "set location= '" + newPos +
                "' where id=" + id ;

        Statement statement = connection.createStatement();

        int updatePos = statement.executeUpdate(sql);
        closeConnection();
        if(updatePos >0)
            return true;

        return false;
    }

    //PRODUCT DB
    public boolean insertProduct(Product prod) throws SQLException {
        openConnection();
        String sql = "INSERT INTO Product values('"+ prod.getRfid()+ "', '"+prod.getBarcode()+"', "+prod.getSaleId()+")";
        Statement statement = connection.createStatement();

        int insert = statement.executeUpdate(sql);
        closeConnection();
        if(insert >0)
            return true;

        return false;
    }

    public boolean deleteProduct(String RFID) throws SQLException {
        openConnection();
        String sql = "DELETE FROM Product WHERE RFID = '" + RFID + "'";
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public boolean updateProductSaleId(String RFID, int saleId) throws SQLException {
        openConnection();
        String sql = "UPDATE Product SET saleId=" + saleId + " WHERE RFID = '" + RFID + "'";
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public void deleteAllProducts() throws SQLException {
        openConnection();
        String sql = "DELETE FROM Product";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    public TreeMap<String, Product> getAllProduct() throws SQLException {
        openConnection();
        String sql = "SELECT * FROM Product";
        TreeMap<String, Product> retList = new TreeMap<>();
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);
        while(res.next()){
            String RFID = res.getString("RFID");
            retList.put(RFID, new Product(res.getString("RFID"), res.getString("barcode"), res.getInt("saleId")));
        }
        closeConnection();
        return retList;
    }

    public Product getProduct(String RFID) throws SQLException {
        openConnection();
        String sql = "SELECT * FROM Product WHERE RFID='"+RFID+"'";
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);

        Product ret;

        if(!res.next())
            ret = null;
        else
            ret = new Product(res.getString("RFID"), res.getString("barcode"), res.getInt("saleId"));

        closeConnection();
        return ret;
    }

    // ORDERS DB

    /**
     * deletes all the content of Order table in DB
     * @throws SQLException
     */
    public void deleteOrders() throws SQLException {
        openConnection();
        String sql = "DELETE FROM Orders";
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    /**
     * This method creates a Statement object for sending a query to EZShop.db. It selects all the
     * orders, which are returned in a single ResultSet object. The TreeMap of orders is filled with
     * the elements returned
     *
     * @return a collection of the orders in db, empty if there are no orders stored.
     * @throws SQLException if a db error occurs or the connection is closed
     */
    public TreeMap<Integer, Order> getOrders () throws SQLException {

        openConnection();

        TreeMap<Integer, Order> orders = new TreeMap<>();

        String sql = "select * from Orders";
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while(resultSet.next()) {
            Integer id = resultSet.getInt("id");
            String productCode = resultSet.getString("productCode");
            double pricePerUnit = resultSet.getDouble("pricePerUnit");
            int quantity = resultSet.getInt("quantity");
            String status = resultSet.getString("status");
            Integer balanceId = resultSet.getInt("balanceId");

            Order o = new OrderImpl(id, productCode, pricePerUnit, quantity);
            o.setStatus(status);
            o.setBalanceId(balanceId);

            orders.put(id, o);
        }

        closeConnection();
        return orders;
    }

    /**
     * Inserts an Order object in Orders table in DB.
     * @param order is the order to insert
     * @throws SQLException if DB already contains an order with same id
     */
    public void insertOrder(Order order) throws SQLException {

        if(getOrders().containsKey(order.getOrderId())) {
            throw new SQLException("Order already in DB");
        }

        openConnection();

        String sql = "INSERT INTO Orders" +
                "(id, productCode, pricePerUnit, quantity, status) VALUES(?,?,?,?,?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        pstmt.setInt(1, order.getOrderId());
        pstmt.setString(2, order.getProductCode());
        pstmt.setDouble(3, order.getPricePerUnit());
        pstmt.setInt(4, order.getQuantity());
        pstmt.setString(5, order.getStatus());
        pstmt.executeUpdate();

        closeConnection();
    }

    /**
     * Updates the fields of an order in table Orders
     * @param o
     * @throws SQLException if o is not in the DB
     */
    public void updateOrder(Order o) throws SQLException {

        if(!getOrders().containsKey(o.getOrderId())){
            throw new SQLException("Order not in DB");
        }

        openConnection();

        String sql = "UPDATE Orders SET status = ? , balanceId = ? "
                + "WHERE id = ?";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        // set the corresponding param
        pstmt.setString(1, o.getStatus());
        pstmt.setInt(2, o.getBalanceId());
        pstmt.setInt(3, o.getOrderId());
        // update
        pstmt.executeUpdate();

        closeConnection();
    }

    // BALANCE OPERATION DB

    /**
     * deletes all the content of BalanceOperation table in DB
     * @throws SQLException
     */
    public void deleteBalanceOperations() throws SQLException {
        openConnection();
        String sql = "DELETE FROM BalanceOperation";
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    public TreeMap<Integer, BalanceOperation> getBalanceOperations () throws SQLException {

        openConnection();

        TreeMap<Integer, BalanceOperation> balanceOperations = new TreeMap<>();

        String sql = "select * from BalanceOperation";
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while(resultSet.next()) {
            Integer id = resultSet.getInt("id");
            LocalDate date = LocalDate.parse(resultSet.getString("date"));
            double money = resultSet.getDouble("money");
            String type = resultSet.getString("type");

            BalanceOperation b = new BalanceOperationImpl(id, date, money, type);

            balanceOperations.put(id, b);
        }
        closeConnection();
        return balanceOperations;
    }

    public void insertBalanceOperation(BalanceOperation balanceOperation) throws SQLException {

        if(getBalanceOperations().containsKey(balanceOperation.getBalanceId())) {
            throw new SQLException("BalanceOperation already in DB");
        }

        openConnection();

        String sql = "INSERT INTO BalanceOperation" +
                "(id, date, money, type) VALUES(?,?,?,?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        pstmt.setInt(1, balanceOperation.getBalanceId());
        pstmt.setString(2, balanceOperation.getDate().toString());
        pstmt.setDouble(3, balanceOperation.getMoney());
        pstmt.setString(4, balanceOperation.getType());
        pstmt.executeUpdate();
        closeConnection();
    }

    // TICKET ENTRY DB

    public List<TicketEntry> getTicketEntries(Integer id) throws SQLException {

        openConnection();

        List<TicketEntry> entries = new ArrayList<>();

        String sql = "select * from TicketEntry where transactionId=" + id ;
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while(resultSet.next()) {
            Integer transactionId = resultSet.getInt("transactionId");
            String barcode = resultSet.getString("barcode");
            int amount = resultSet.getInt("amount");
            double pricePerUnit = resultSet.getDouble("pricePerUnit");
            double discountRate = resultSet.getDouble("discountRate");
            String description = resultSet.getString("description");

            TicketEntry t = new TicketEntryImpl(barcode, description, amount, pricePerUnit, discountRate);
            entries.add(t);
        }
        closeConnection();
        return entries;
    }

    public void insertTicketEntry(Integer transactionId, TicketEntry ticketEntry) throws SQLException {

        openConnection();

        String sql = "INSERT INTO TicketEntry" +
                "(transactionId, barcode, amount, pricePerUnit, discountRate, description) VALUES(?,?,?,?,?,?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        pstmt.setInt(1, transactionId);
        pstmt.setString(2, ticketEntry.getBarCode());
        pstmt.setInt(3, ticketEntry.getAmount());
        pstmt.setDouble(4, ticketEntry.getPricePerUnit());
        pstmt.setDouble(5, ticketEntry.getDiscountRate());
        pstmt.setString(6, ticketEntry.getProductDescription());
        pstmt.executeUpdate();

        closeConnection();
    }

    public void updateTicketEntry(Integer tId, TicketEntry ticketEntry) throws SQLException {
        openConnection();
        String sql = "UPDATE TicketEntry SET amount = ? , "
                + "pricePerUnit = ? ,"
                + "discountRate = ? ,"
                + "description = ? "
                + "WHERE (transactionId = ? AND barcode = ?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        // set the corresponding param
        pstmt.setInt(1, ticketEntry.getAmount());
        pstmt.setDouble(2, ticketEntry.getPricePerUnit());
        pstmt.setDouble(3, ticketEntry.getDiscountRate());
        pstmt.setString(4, ticketEntry.getProductDescription());
        pstmt.setInt(5, tId);
        pstmt.setString(6, ticketEntry.getBarCode());
        // update
        pstmt.executeUpdate();
        closeConnection();
    }

    /*public void deleteTicketEntry(Integer transactionId, String barcode) throws SQLException {
        openConnection();

        String sql = "DELETE FROM TicketEntry WHERE (transactionId = ? AND barcode = ?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);

        // set the corresponding param
        pstmt.setInt(1, transactionId);
        pstmt.setString(2, barcode);
        // execute the delete statement
        pstmt.executeUpdate();
        closeConnection();
    }*/

    public void deleteTicketEntriesInSale(Integer transactionId) throws SQLException {
        openConnection();
        String sql = "DELETE FROM TicketEntry WHERE transactionId = ?";
        PreparedStatement pstmt = this.connection.prepareStatement(sql);

        // set the corresponding param
        pstmt.setInt(1, transactionId);
        // execute the delete statement
        pstmt.executeUpdate();
        closeConnection();
    }

    public void deleteTicketEntries() throws SQLException {
        openConnection();
        String sql = "DELETE FROM TicketEntry";
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    // RETURNED PRODUCT DB

    /**
     * Deletes the whole content of ReturnedProduct table
     * @throws SQLException
     */
    public void deleteAllReturnedProducts() throws SQLException {
        openConnection();
        String sql = "DELETE FROM ReturnedProduct";
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    /**
     *
     * @param id the id of the Return Transaction
     * @return the list of products returned in a specific Return Transaction
     * @throws SQLException
     */
    public List<TicketEntry> getReturnedProducts(Integer id) throws SQLException {

        openConnection();

        List<TicketEntry> entries = new ArrayList<>();

        String sql = "select * from ReturnedProduct where transactionId=" + id ;
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while(resultSet.next()) {
            Integer transactionId = resultSet.getInt("transactionId");
            String barcode = resultSet.getString("barcode");
            int amount = resultSet.getInt("amount");
            double pricePerUnit = resultSet.getDouble("pricePerUnit");
            double discountRate = resultSet.getDouble("discountRate");
            String description = resultSet.getString("description");

            TicketEntry t = new TicketEntryImpl(barcode, description, amount, pricePerUnit, discountRate);
            entries.add(t);
        }
        closeConnection();
        return entries;
    }

    public void insertReturnedProduct(Integer transactionId, TicketEntry ticketEntry) throws SQLException {

        if(getReturnedProducts(transactionId).stream()
                        .map(TicketEntry::getBarCode)
                        .anyMatch(ticketEntry.getBarCode()::equals)) {
            throw new SQLException("Returned product already exists in Return transaction");
        }
        openConnection();
        String sql = "INSERT INTO ReturnedProduct" +
                "(transactionId, barcode, amount, pricePerUnit, discountRate, description) VALUES(?,?,?,?,?,?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        pstmt.setInt(1, transactionId);
        pstmt.setString(2, ticketEntry.getBarCode());
        pstmt.setInt(3, ticketEntry.getAmount());
        pstmt.setDouble(4, ticketEntry.getPricePerUnit());
        pstmt.setDouble(5, ticketEntry.getDiscountRate());
        pstmt.setString(6, ticketEntry.getProductDescription());
        pstmt.executeUpdate();

        closeConnection();
    }

    public void updateReturnedProduct(Integer tId, TicketEntry ticketEntry) throws SQLException {

        if(!getReturnTransactions().containsKey(tId) &&
                !getReturnedProducts(tId).stream()
                        .map(TicketEntry::getBarCode)
                        .anyMatch(ticketEntry.getBarCode()::equals)) {
            throw new SQLException("Returned product doesn't exists in specified Return transaction");
        }

        openConnection();

        String sql = "UPDATE ReturnedProduct SET amount = ? , "
                + "pricePerUnit = ? , "
                + "discountRate = ? ,"
                + "description = ? "
                + "WHERE (transactionId = ? AND barcode = ?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        // set the corresponding param
        pstmt.setInt(1, ticketEntry.getAmount());
        pstmt.setDouble(2, ticketEntry.getPricePerUnit());
        pstmt.setDouble(3, ticketEntry.getDiscountRate());
        pstmt.setString(4, ticketEntry.getProductDescription());
        pstmt.setInt(5, tId);
        pstmt.setString(6, ticketEntry.getBarCode());
        // update
        pstmt.executeUpdate();
        closeConnection();
    }

    public void deleteReturnedProduct(Integer transactionId, String barcode) throws SQLException {

        if(!getReturnTransactions().containsKey(transactionId) &&
                getReturnedProducts(transactionId).stream()
                        .map(TicketEntry::getBarCode)
                        .noneMatch(barcode::equals)) {
            throw new SQLException("Returned product doesn't exists in specified Return transaction");
        }

        openConnection();

        String sql = "DELETE FROM ReturnedProduct WHERE (transactionId = ? AND barcode = ?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);

        // set the corresponding param
        pstmt.setInt(1, transactionId);
        pstmt.setString(2, barcode);
        // execute the delete statement
        pstmt.executeUpdate();

        closeConnection();
    }

    public void deleteReturnedProductsInReturn(Integer transactionId) throws SQLException {

        openConnection();

        String sql = "DELETE FROM ReturnedProduct WHERE transactionId = ?";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);

        // set the corresponding param
        pstmt.setInt(1, transactionId);
        // execute the delete statement
        pstmt.executeUpdate();

        closeConnection();
    }

    // SALE TRANSACTION DB

    public TreeMap<Integer, SaleTransaction> getSaleTransactions () throws SQLException {

        openConnection();

        TreeMap<Integer, SaleTransaction> saleTransactions = new TreeMap<>();

        String sql = "select * from SaleTransaction";
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            Integer balanceId = resultSet.getInt("balanceId");
            double discountRate = resultSet.getDouble("discountRate");
            double price = resultSet.getDouble("price");
            String state = resultSet.getString("state");

            SaleTransactionImpl st = new SaleTransactionImpl(id);
            st.setDiscountRate(discountRate);
            st.setPrice(price);
            st.setState(state);
            st.setBalanceId(balanceId);

            saleTransactions.put(id, st);
        }
        closeConnection();

        // perchè il metodo getTicketEntries apre e chiude una nuova connessione
        for(SaleTransaction st : saleTransactions.values()){
            st.setEntries(getTicketEntries(st.getTicketNumber()));
        }

        return saleTransactions;
    }

    public void insertSaleTransaction(SaleTransactionImpl saleTransaction) throws SQLException{

        openConnection();
        String sql = "INSERT INTO SaleTransaction" +
                "(id, balanceId, discountRate, price, state) VALUES(?,?,?,?,?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        pstmt.setInt(1, saleTransaction.getTicketNumber());
        // avoid null pointer exception when ST is not yet paid
        if(saleTransaction.getBalanceId() != null ){
            pstmt.setInt(2, saleTransaction.getBalanceId());
        }
        pstmt.setDouble(3, saleTransaction.getDiscountRate());
        pstmt.setDouble(4, saleTransaction.getPrice());
        pstmt.setString(5, saleTransaction.getState());
        pstmt.executeUpdate();

        closeConnection();
    }

    public SaleTransaction getSaleTransaction(Integer saleTransactionID) throws SQLException{
        openConnection();
        String sql = "SELECT * FROM SaleTransaction WHERE id=" + saleTransactionID;
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);
        SaleTransactionImpl st;
        //int balanceId = 0;
        if(!res.next())
            return null;
        else {
            st = new SaleTransactionImpl(saleTransactionID);
            st.setBalanceId(res.getInt("balanceId"));
            st.setDiscountRate(res.getDouble("discountRate"));
            st.setPrice(res.getDouble("price"));
            st.setState(res.getString("state"));
            closeConnection();
            st.setEntries(getTicketEntries(saleTransactionID));
            return st;
        }
    }

    public void updateSaleTransaction(SaleTransactionImpl st) throws SQLException {
        openConnection();
        String sql = "UPDATE SaleTransaction SET discountRate = ?, price = ? , "
                + "state = ? "
                + "WHERE id = ?";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        // set the corresponding param
        pstmt.setDouble(1, st.getDiscountRate());
        pstmt.setDouble(2, st.getPrice());
        pstmt.setString(3, st.getState());
        pstmt.setInt(4, st.getTicketNumber());
        // update
        pstmt.executeUpdate();

        if(st.getBalanceId() != null){
            sql = "UPDATE SaleTransaction SET balanceId = ? WHERE id = ?";
            PreparedStatement pstmt2 = this.connection.prepareStatement(sql);
            pstmt2.setInt(1, st.getBalanceId());
            pstmt2.setInt(2, st.getTicketNumber());
            pstmt2.executeUpdate();
        }
        closeConnection();
    }

    public boolean deleteSaleTransaction(Integer saleTransactionId) throws SQLException {
        openConnection();
        String sql = "delete from SaleTransaction where id=" + saleTransactionId;
        Statement statement = connection.createStatement();
        int delete = statement.executeUpdate(sql);
        closeConnection();
        if(delete>0) {
            deleteTicketEntriesInSale(saleTransactionId);
            return true;
        }
        return false;
    }

    public void deleteSaleTransactions() throws SQLException {
        openConnection();
        String sql = "DELETE FROM SaleTransaction";
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    // RETURN TRANSACTION DB

    public void deleteReturnTransactions() throws SQLException {
        openConnection();
        String sql = "DELETE FROM ReturnTransaction";
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    public TreeMap<Integer, ReturnTransaction> getReturnTransactions () throws SQLException {

        openConnection();

        TreeMap<Integer, ReturnTransaction> returnTransactions = new TreeMap<>();

        String sql = "select * from ReturnTransaction";
        Statement statement = this.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while(resultSet.next()) {
            Integer id = resultSet.getInt("id");
            Integer balanceId = resultSet.getInt("balanceId");
            Integer saleTransactionId = resultSet.getInt("saleTransaction");
            double price = resultSet.getDouble("price");
            String state = resultSet.getString("state");

            ReturnTransaction rt = new ReturnTransaction(id, saleTransactionId);
            rt.setBalanceId(balanceId);
            rt.setPrice(price);
            rt.setState(state);

            returnTransactions.put(id, rt);
        }
        closeConnection();

        // perchè viene aperta e chiusa una nuova connessione
        for(ReturnTransaction rt : returnTransactions.values()){
            rt.setEntryList(getReturnedProducts(rt.getId()));
        }

        return returnTransactions;
    }

    public void insertReturnTransaction(ReturnTransaction rt) throws SQLException {

        if(getReturnTransactions().containsKey(rt.getId())){
            throw new SQLException("Return Transaction already exists in DB");
        }

        openConnection();

        String sql = "INSERT INTO ReturnTransaction" +
                "(id, saleTransaction, price, state) VALUES(?,?,?,?)";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        pstmt.setInt(1, rt.getId());
        pstmt.setInt(2, rt.getSaleTransactionId());
        pstmt.setDouble(3, rt.getPrice());
        pstmt.setString(4, rt.getState());
        pstmt.executeUpdate();

        closeConnection();

        for(TicketEntry t : rt.getEntryList()){
            insertReturnedProduct(rt.getId(), t);
        }
    }

    public void updateReturnTransaction(ReturnTransaction rt) throws SQLException {

        if(!getReturnTransactions().containsKey(rt.getId())){
            throw new SQLException("Return Transaction does not exist in DB");
        }

        openConnection();

        String sql = "UPDATE ReturnTransaction SET price = ? , "
                + "state = ? "
                + "WHERE id = ?";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);
        // set the corresponding param
        pstmt.setDouble(1, rt.getPrice());
        pstmt.setString(2, rt.getState());
        pstmt.setInt(3, rt.getId());
        // update
        pstmt.executeUpdate();

        if(rt.getBalanceId() != null){
            sql = "UPDATE ReturnTransaction SET balanceId = ? WHERE id = ?";
            PreparedStatement pstmt2 = this.connection.prepareStatement(sql);
            pstmt2.setInt(1, rt.getBalanceId());
            pstmt2.setInt(2, rt.getId());
            pstmt2.executeUpdate();
        }
        closeConnection();
    }

    public void deleteReturnTransaction(Integer id) throws SQLException {

        if(!getReturnTransactions().containsKey(id)) {
            throw new SQLException("Return Transaction does not exist in DB");
        }
        deleteReturnedProductsInReturn(id);

        openConnection();
        String sql = "DELETE FROM ReturnTransaction WHERE id = ?";

        PreparedStatement pstmt = this.connection.prepareStatement(sql);

        // set the corresponding param
        pstmt.setInt(1, id);
        // execute the delete statement
        pstmt.executeUpdate();
        closeConnection();
    }

    /* USER DB METHODS */

   public void deleteUsers() throws SQLException {
       openConnection();
        String sql = "DELETE FROM User";
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    public boolean insertUser(User user) throws SQLException {
       openConnection();
        String sql = "INSERT INTO User VALUES("+user.getId()+",'"+user.getUsername()+"','"+user.getPassword() +"','"+user.getRole()+"')";
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public UserImpl getUser(Integer id) throws SQLException {
       openConnection();
        String sql = "SELECT * FROM User WHERE id = '" + id + "'";
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);

        UserImpl ret;

        if(!res.next())
            ret = null;
        else
            ret = new UserImpl(res.getInt("id"), res.getString("username"), res.getString("password"),  res.getString("role"));

        closeConnection();
        return ret;

    }

    public TreeMap<Integer, User> getAllUser() throws SQLException {
       openConnection();
        String sql = "SELECT * FROM User";
        TreeMap<Integer, User> retList = new TreeMap<>();
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);
        while(res.next()){
            Integer id = res.getInt("id");
            retList.put(id, new UserImpl(res.getInt("id"), res.getString("username"), res.getString("password"),  res.getString("role")));
        }
        closeConnection();
        return retList;
    }

    public boolean deleteUser(Integer id) throws SQLException {
       openConnection();
        String sql = "DELETE FROM User WHERE id = " + id;
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public boolean updateUserRight(Integer id, String newRole) throws SQLException {
       openConnection();
        String sql = "UPDATE User SET role='" + newRole + "' WHERE id=" + id;
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    /* CUSTOMER DB METHODS */
    public boolean insertCustomer(Customer customer) throws SQLException {
        openConnection();
        String sql = "INSERT INTO Customer VALUES("+customer.getId()+", '"+customer.getCustomerName()+"', NULL, 0)";
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public Customer getCustomer(Integer id) throws SQLException {
        openConnection();
        String sql = "SELECT * FROM Customer WHERE id = " + id;
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);
        Customer ret;
        if(!res.next())
            ret = null;
        else
            ret = new CustomerImpl(res.getInt("id"), res.getString("name"), res.getString("card"),  res.getInt("points"));
        closeConnection();
        return ret;
    }

    public TreeMap<Integer, Customer> getAllCustomer() throws SQLException {
        openConnection();
        String sql = "SELECT * FROM Customer";
        TreeMap<Integer, Customer> retList = new TreeMap<>();
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);
        while(res.next()){
            Integer id = res.getInt("id");
            retList.put(id, new CustomerImpl(res.getInt("id"), res.getString("name"), res.getString("card"),  res.getInt("points")));
        }
        closeConnection();
        return retList;
    }

    public boolean deleteCustomer(Integer id) throws SQLException {
        openConnection();
        String sql = "DELETE FROM Customer WHERE id = " + id;
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public void deleteCustomers() throws SQLException {
        openConnection();
        String sql = "DELETE FROM Customer";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    public boolean updateCustomerPoints(Integer id, Integer points) throws SQLException {
        openConnection();
        String sql = "UPDATE Customer SET points=points+" + points + " WHERE id=" + id;
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public boolean updateCustomerName(Integer id, String name) throws SQLException {
        openConnection();
        String sql = "UPDATE Customer SET name='" + name + "' WHERE id=" + id;
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public boolean updateCustomerCard(Integer id, String card) throws SQLException {
        openConnection();
        String sql = "UPDATE Customer SET card=" + card + " WHERE id=" + id;
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public boolean updateCustomer(Integer id, String name, String card) throws SQLException {
        String sql;

        if(card != null) {
            sql = "UPDATE Customer SET card='" + card + "', name='" + name + "' WHERE id=" + id;
        }
        else {
            sql = "UPDATE Customer SET card=NULL, name='" + name + "' WHERE id=" + id;
        }

        openConnection();
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public LoyaltyCardImpl getLoyaltyCard(String code) throws SQLException{
        openConnection();
        String sql = "SELECT * FROM LoyaltyCard WHERE code = '" + code + "'";
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);

        LoyaltyCardImpl card;
        if(!res.next())
            card =  null;
        else
            card = new LoyaltyCardImpl(res.getString("code"), res.getBoolean("assigned"));
        closeConnection();
        return card;

    }

    public boolean insertLoyaltyCard(LoyaltyCardImpl card) throws SQLException{
        openConnection();
        String sql = "INSERT INTO LoyaltyCard VALUES('"+card.getCode()+"', "+card.isAssigned()+")";
        Statement statement = connection.createStatement();
        boolean ret = statement.executeUpdate(sql) > 0;
        closeConnection();
        return ret;
    }

    public void deleteLoyaltyCards() throws SQLException{
        openConnection();
        String sql = "DELETE FROM LoyaltyCard";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    public String getAssignableLoyaltyCardCode() throws SQLException{
        openConnection();
        String sql = "SELECT * FROM LoyaltyCard WHERE assigned = FALSE LIMIT 1";
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);

        String ret;
        if(!res.next())
            ret = null;
        else
            ret = res.getString("code");
        closeConnection();
        return ret;
    }

    public void updateCardAssigned(String code, boolean isAssigned) throws SQLException {
        openConnection();
        String sql = "UPDATE LoyaltyCard SET assigned = "+isAssigned+" WHERE code='"+code+"'";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        closeConnection();
    }

    public String getLastCard() throws SQLException {
        openConnection();
        String sql = "SELECT * FROM LoyaltyCard ORDER BY code DESC LIMIT 1;";
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(sql);

        String ret;
        if(!res.next())
            ret = null;
        else
            ret = res.getString("code");
        closeConnection();
        return ret;
    }
}

