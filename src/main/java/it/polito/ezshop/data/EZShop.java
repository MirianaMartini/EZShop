package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


public class EZShop implements EZShopInterface {

    private ArrayList<ProductType> inventory;
    private TreeMap<Integer, Order> orders;
    private TreeMap<Integer, BalanceOperation> balanceOperations;
    private TreeMap<Integer, User> users = new TreeMap<>();
    //private TreeMap<Integer, UserImpl> users = new TreeMap<>();
    private User loggedUser;
    private TreeMap<Integer, Customer> customers = new TreeMap<>();
    private TreeMap<Integer, SaleTransaction> saleTransactions;
    private TreeMap<Integer, ReturnTransaction> returnTransactions;
    private TreeMap<String, Product> products;

    private EZShopDB ezshopdb;

    private static Integer orderCounter;
    private static Integer balanceOperationCounter;
    private static Integer userCounter;
    private static Integer saleTransactionCounter;
    private static Integer returnTransactionCounter;
    private static Integer customerCounter;

    /**
     *  Check length of barcode for validity via the checkdigit calculation
     * We split the barcode into its constituent digits, offset them into the GTIN
     * calculation tuple (x1, x3, x1, x3, x1, etc, etc), multiply the digits and add
     * them together, then modulo them on 10, and you get the calculated check digit.
     * For more information see GS1 website: https://www.gs1.org/services/how-calculate-check-digit-manually
     * @param code is the barcode
     * @return TRUE if is a valid GTIN 12,13 or 14 barcode, FALSE otherwise
     * */
    public static boolean validBarcode(String code){

        if (code!=null && !code.isEmpty() && Pattern.matches("[0-9]{12,14}", code)) {
            int[] checkDigitArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            int[] gtinMaths = { 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3 };
            String[] barcodeArray = code.split("(?!^)");
            int gtinLength = code.length();
            int modifier = (17 - (gtinLength - 1));
            int gtinCheckDigit = Integer.parseInt(code.substring(gtinLength - 1));
            int tmpCheckSum = 0;

            // Run through and put digits into multiplication table
            for (int i = 0; i < (gtinLength - 1); i++) {
                checkDigitArray[modifier + i] = Integer.parseInt(barcodeArray[i]); // Add barcode digits to
                // Multiplication Table
            }
            // Calculate "Sum" of barcode digits
            for (int i = modifier; i < 17; i++) {
                tmpCheckSum += (checkDigitArray[i] * gtinMaths[i]);
            }

            // Check if last digit is correct
            return gtinCheckDigit == (int) ((Math.ceil((float) tmpCheckSum / (float) 10) * 10) - tmpCheckSum);
        }
        return false;
    }

    public static boolean validRFID(String rfid){
        if(rfid != null && Pattern.matches("[0-9]{12}", rfid)) {
            int id = Integer.parseInt(rfid);
            return id > 0;
        }
        return false;
    }

    /**
     *
     * @param requiredRole the authorization level needed
     * @return TRUE if the logged user has the right authorization level to perform the action, FALSE otherwise
     */
    public boolean checkAuth(String requiredRole){
        //UserImpl loggedUser = users.get(loggedUserId);

        if(loggedUser != null) {

            switch (requiredRole) {
                case "Administrator":  //Only Administrator
                    return loggedUser.getRole().equals("Administrator");
                case "ShopManager":   //Shop Manager and Administrator
                    return loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager");
                case "Cashier":  //Cashier, ShopManager, Administrator
                    return loggedUser.getRole().equals("Administrator") || loggedUser.getRole().equals("ShopManager") || loggedUser.getRole().equals("Cashier");
                default:
                    return false;
            }
        }
        return false;
    }

    public static boolean checkCustomerCard(String card){
        return card != null && card.length() == 10 && card.matches("^[0-9]*$");
    }

/**
* Check the validity of the product position string which should be: 
* <aisleNumber> - <rackAlphabeticIdentifier> - <levelNumber> 
**/
    public static boolean validPosition(String position){
        return Pattern.matches("[1-9][0-9]*-[a-zA-Z]-[1-9][0-9]*", position);
    }

    public static boolean checkLuhn(String cardNo) {
        if(cardNo!=null && !cardNo.isEmpty() && Pattern.matches("[0-9]{16}", cardNo)) {
            int nDigits = cardNo.length();

            int nSum = 0;
            boolean isSecond = false;
            for (int i = nDigits - 1; i >= 0; i--) {
                int d = cardNo.charAt(i) - '0';
                if (isSecond)
                    d = d * 2;
                // We add two digits to handle
                // cases that make two digits
                // after doubling
                nSum += d / 10;
                nSum += d % 10;
                isSecond = !isSecond;
            }
            return (nSum % 10 == 0);
        }
        return false;
    }

    public static String[] getCreditCards(String card) throws IOException {
        String[] nofind={""};
        if(card == null || card.isEmpty())
            return nofind;

        FileReader FileCredit = new FileReader("CreditCard.txt");
        BufferedReader reader=new BufferedReader(FileCredit);
        String line= reader.readLine();
        while(line!=null){
            if(line.charAt(0)!='#'){
                String[] splits = line.split(";");
                if(splits[0].equals(card)) {
                    FileCredit.close();
                    return splits;
                }
            }
            line= reader.readLine();
        }
        FileCredit.close();
        return nofind;
    }


    public EZShop() {

        try {
            ezshopdb = new EZShopDB();

            this.inventory = ezshopdb.getProducts();

            this.products = ezshopdb.getAllProduct();

            orderCounter=0;
            this.orders = ezshopdb.getOrders();
            if(!orders.isEmpty()){
                orderCounter = orders.lastKey();
            }

            balanceOperationCounter=0;
            this.balanceOperations = ezshopdb.getBalanceOperations();
            if(!balanceOperations.isEmpty()){
                balanceOperationCounter = balanceOperations.lastKey();
            }

            saleTransactionCounter=0;
            this.saleTransactions = ezshopdb.getSaleTransactions();
            if(!saleTransactions.isEmpty()){
                saleTransactionCounter = saleTransactions.lastKey();
            }

            returnTransactionCounter=0;
            this.returnTransactions = ezshopdb.getReturnTransactions();
            if(!returnTransactions.isEmpty()){
                returnTransactionCounter = returnTransactions.lastKey();
            }

            userCounter=0;
            this.users = ezshopdb.getAllUser();
            if(!users.isEmpty()){
                userCounter = users.lastKey();
            }

            customerCounter=0;
            this.customers = ezshopdb.getAllCustomer();
            if(!customers.isEmpty()){
                customerCounter = customers.lastKey();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void reset() {

        try{
            ezshopdb.resetDB();
            inventory.clear();
            saleTransactions.clear();
            returnTransactions.clear();
            orders.clear();
            balanceOperations.clear();
            users.clear();
            customers.clear();

            saleTransactionCounter=0;
            returnTransactionCounter=0;
            orderCounter=0;
            balanceOperationCounter=0;
            userCounter=0;
            customerCounter=0;
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {

        if (username == null || username.trim().isEmpty())
            throw new InvalidUsernameException();

        if (password == null || password.trim().isEmpty())
            throw new InvalidPasswordException();

        if(role == null || !(role.equals("Administrator") || role.equals("Cashier") || role.equals("ShopManager")))
            throw new InvalidRoleException();

        for(User user : users.values()){
            if(user.getUsername().equals(username))
                return -1;
        }

        try {

            UserImpl u = new UserImpl(++userCounter, username, password, role);
            if(ezshopdb.insertUser(u)) {
                users.put(u.getId(), u);
                return u.getId();
            }
            else
                return -1;

        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {

        if(!checkAuth("Administrator"))
            throw new UnauthorizedException();

        if(id == null || id <= 0)
            throw new InvalidUserIdException();

        try {
            if(ezshopdb.deleteUser(id)) {
                users.remove(id);
                return true;
            }
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {

        if(!checkAuth("Administrator"))
            throw new UnauthorizedException();

        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(!checkAuth("Administrator"))
            throw new UnauthorizedException();

        if(id == null || id <= 0)
            throw new InvalidUserIdException();

        return users.get(id);

    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {

        if(!checkAuth("Administrator"))
            throw new UnauthorizedException();

        if(role == null || !(role.equals("Administrator") || role.equals("Cashier") || role.equals("ShopManager")))
            throw new InvalidRoleException();

        if(id == null || id <= 0)
            throw new InvalidUserIdException();

        try {
            if(ezshopdb.updateUserRight(id, role)){
                User u = users.get(id);
                u.setRole(role);
                return users.replace(id, u) != null;
            }
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {

        if (username == null || username.trim().isEmpty())
            throw new InvalidUsernameException();

        if (password == null || password.trim().isEmpty())
            throw new InvalidPasswordException();

        for(User u : users.values()){
            if(u.getPassword().equals(password) && u.getUsername().equals(username)){
                loggedUser = u;
                return loggedUser;
            }
        }
        return null;
    }

    @Override
    public boolean logout() {
        if(loggedUser == null)
            return false;

        loggedUser = null;
        return true;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {

        if (description == null || description.isEmpty())
            throw new InvalidProductDescriptionException();
        if(productCode==null || productCode.isEmpty() || !validBarcode(productCode))
            throw new InvalidProductCodeException();
        if (pricePerUnit<=0)
            throw new InvalidPricePerUnitException();
        if(!checkAuth("ShopManager")) {
            throw new UnauthorizedException();
        }


        int new_id=inventory.size();
        if(new_id > 0) {
            new_id = inventory.get(inventory.size() - 1).getId();
            new_id++;
        }
        else{
            new_id++;
        }

        //check barcode
        for(ProductType p: inventory){
            if(p.getBarCode().equals(productCode))
                return -1;
        }

        ProductTypeImpl product = new ProductTypeImpl(new_id, description, productCode, pricePerUnit, note);

        try {
            ezshopdb.insertProductTypeDB(product);
            inventory.add(product);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }

        return product.getId();

    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        if (id == null || id<=0)
            throw new InvalidProductIdException();
        if (newDescription==null || newDescription.isEmpty())
            throw new InvalidProductDescriptionException();
        if(newCode==null || newCode.isEmpty() || !validBarcode(newCode))
            throw new InvalidProductCodeException();
        if (newPrice<=0)
            throw new InvalidPricePerUnitException();
        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        for (ProductType p : inventory) {
            if (p.getBarCode().equals(newCode) && !p.getId().equals(id))
                return false;
        }

        for (ProductType p : inventory) {
            if (p.getId().equals(id)) {
                try {
                    p.setProductDescription(newDescription);
                    p.setBarCode(newCode);
                    p.setPricePerUnit(newPrice);
                    p.setNote(newNote);
                    ezshopdb.updateProductTypeDB(p);
                    return true;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }
        return false;

    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        if (id==null || id<=0) throw new InvalidProductIdException();
        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        for (ProductType p : inventory) {
            if (p.getId().equals(id)) {
                inventory.remove(p);
                try {
                    ezshopdb.deleteProductTypeDB(id);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                return true;
            }
        }
        return false;

    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        return inventory;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        if(barCode==null || barCode.isEmpty() || !validBarcode(barCode) )
            throw new InvalidProductCodeException();
        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        for (ProductType p : inventory) {
            if (p.getBarCode().equals(barCode))
                return p;
        }
        return null;

    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        String match = (description == null ? "" : description);

        ArrayList<ProductType> list = new ArrayList<>();
        for (ProductType p : inventory) {
            if (p.getProductDescription().toLowerCase().contains(match.toLowerCase()))
                list.add(p);
        }
        return list;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        if ( productId==null || productId<=0 )
            throw new InvalidProductIdException();
        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();


            for (ProductType p : inventory) {
                if (p.getId().equals(productId)) {
                    int newquantity = p.getQuantity() + toBeAdded;
                    if (newquantity >= 0 && p.getLocation() != null) {
                        p.setQuantity(newquantity);
                        try {
                            ezshopdb.updateQuantityDB(productId, newquantity);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                }
            }
        return false;
    }

     @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        if (productId==null || productId<=0 )
            throw new InvalidProductIdException();
        if(!validPosition(newPos))
            throw new InvalidLocationException();
        if(!checkAuth("ShopManager")) {
            throw new UnauthorizedException();
        }

        ProductType prod = null;
        for (ProductType p : inventory) {
            if (p.getLocation() != null && p.getLocation().toLowerCase().equals(newPos.toLowerCase()))
                return false;
            if (p.getId().equals(productId))
                prod = p;
        }
        if(prod == null) {
            return false;
        }

        try {
            prod.setLocation(newPos);
            ezshopdb.updatePositionDB(productId, newPos);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;

    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {

        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        if(productCode==null || productCode.isEmpty() || !validBarcode(productCode)) {
            throw new InvalidProductCodeException();
        }

        if(quantity <= 0)
            throw new InvalidQuantityException();


        if(pricePerUnit <= 0)
            throw new InvalidPricePerUnitException();


        if(getProductTypeByBarCode(productCode)==null)
            return -1;


        Integer id = ++orderCounter;
        Order o = new OrderImpl(id, productCode, pricePerUnit, quantity);
        orders.put(id, o);
        try {
            ezshopdb.insertOrder(o);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }

        return id;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {

        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        if(productCode==null || productCode.isEmpty() || !validBarcode(productCode)){
            throw new InvalidProductCodeException();
        }
        if(quantity <= 0)
            throw new InvalidQuantityException();

        if(pricePerUnit <= 0){
            throw new InvalidPricePerUnitException();
        }

        if(getProductTypeByBarCode(productCode)==null ||
                ( computeBalance() - (quantity*pricePerUnit)<0) ){
            return -1;
        }

        Integer id = ++orderCounter;
        Order o = new OrderImpl(id, productCode, pricePerUnit, quantity);

        recordBalanceUpdate(-(quantity*pricePerUnit));
        o.setBalanceId(balanceOperationCounter);
        o.setStatus("PAYED");

        orders.put(id, o);
        try {
            ezshopdb.insertOrder(o);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }

        return id;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {

        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        if(orderId == null || orderId <= 0){
            throw new InvalidOrderIdException();
        }

        Order o = orders.get(orderId);

        if( (o == null) ||
                ( !(o.getStatus().equals("PAYED")) && !(o.getStatus().equals("ISSUED")) ) ){
            return false;
        } else {
            if (o.getStatus().equals("ISSUED")) {
                int quantity = o.getQuantity();
                double pricePerUnit = o.getPricePerUnit();

                if(!recordBalanceUpdate(-(quantity * pricePerUnit)))
                    return false;

                o.setBalanceId(balanceOperationCounter);
                o.setStatus("PAYED");
                try {
                    ezshopdb.updateOrder(o);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {

        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        if(orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException();
        }

        Order o = orders.get(orderId);
        if(o==null){
            return false;
        }

        if(  !(o.getStatus().equals("PAYED")) && !(o.getStatus().equals("COMPLETED"))  ){
            return false;
        }
        else {
            if(o.getStatus().equals("PAYED")) {

                /* retrieve product for order */
                ProductType p;
                try {
                    p = getProductTypeByBarCode(o.getProductCode());
                }
                catch (InvalidProductCodeException e) {
                    e.printStackTrace();
                    return false;
                }

                if(p.getLocation() == null){
                    throw new InvalidLocationException();
                }
                try {
                    updateQuantity(p.getId(), (o.getQuantity()) );
                } catch (InvalidProductIdException e) {
                    e.printStackTrace();
                }
                o.setStatus("COMPLETED");

                try {
                    ezshopdb.updateOrder(o);
                }
                catch (SQLException throwables) {
                    throwables.printStackTrace();
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * This method records the arrival of an order with given <orderId>. This method changes the quantity of available product.
     * This method records each product received, with its RFID. RFIDs are recorded starting from RFIDfrom, in increments of 1
     * ex recordOrderArrivalRFID(10, "000000001000")  where order 10 ordered 10 quantities of an item, this method records
     * products with RFID 1000, 1001, 1002, 1003 etc until 1009
     * The product type affected must have a location registered. The order should be either in the PAYED state (in this
     * case the state will change to the COMPLETED one and the quantity of product type will be updated) or in the
     * COMPLETED one (in this case this method will have no effect at all).
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param orderId the id of the order that has arrived
     *
     * @return  true if the operation was successful
     *          false if the order does not exist or if it was not in an ORDERED/COMPLETED state
     *
     * @throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
     * @throws InvalidLocationException if the ordered product type has not an assigned location.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     * @throws InvalidRFIDException if the RFID has invalid format or is not unique
     */
    @Override
    public boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom) throws InvalidOrderIdException, UnauthorizedException, 
InvalidLocationException, InvalidRFIDException {
        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        if(orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException();
        }

        if(RFIDfrom == null || RFIDfrom.isEmpty() || !validRFID(RFIDfrom))
            throw new InvalidRFIDException();

        Order o = orders.get(orderId);
        if(o==null){
            return false;
        }

        /* if RFID from already exists */
        int start = Integer.parseInt(RFIDfrom);
        for(int i=0; i<o.getQuantity(); i++){
            String rfid = String.format("%012d", start + i);
            if(products.containsKey(rfid))
                throw new InvalidRFIDException();
        }

        if(  !(o.getStatus().equals("PAYED")) && !(o.getStatus().equals("COMPLETED"))  ){
            return false;
        }
        else {
            if(o.getStatus().equals("PAYED")) {

                /* retrieve product for order */
                ProductType p;
                try {
                    p = getProductTypeByBarCode(o.getProductCode());
                }
                catch (InvalidProductCodeException e) {
                    e.printStackTrace();
                    return false;
                }

                if(p.getLocation() == null){
                    throw new InvalidLocationException();
                }

                /* add products with RFID */
                int RFIDstart = Integer.parseInt(RFIDfrom);
                for(int i=0; i<o.getQuantity(); i++){
                    String RFIDstring = String.format("%012d", RFIDstart + i);
                    Product product = new Product(RFIDstring, p.getBarCode());
                    products.put(RFIDstring, product);

                    try {
                        ezshopdb.insertProduct(product);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        return false;
                    }
                }

                try {
                    updateQuantity(p.getId(), (o.getQuantity()) );
                } catch (InvalidProductIdException e) {
                    e.printStackTrace();
                }
                o.setStatus("COMPLETED");

                try {
                    ezshopdb.updateOrder(o);
                }
                catch (SQLException throwables) {
                    throwables.printStackTrace();
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {

        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        return new ArrayList<>(orders.values());
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {

        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        if(customerName == null || customerName.trim().isEmpty())
            throw new InvalidCustomerNameException();

        for(Customer customer : customers.values()){
            if(customer.getCustomerName().equals(customerName))
                return -1;
        }

        try {
//            if(ezshopdb.getCustomer(customerName) != null)
//                return -1;

            CustomerImpl c = new CustomerImpl(++customerCounter, customerName);
            if(ezshopdb.insertCustomer(c)) {
                customers.put(c.getId(), c);
                return c.getId();
            }
            else
                return -1;

        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {

        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        if(id == null || id <= 0)
            throw new InvalidCustomerIdException();

        if(newCustomerName == null || newCustomerName.trim().isEmpty())
            throw new InvalidCustomerNameException();

        try {
            Customer c = customers.get(id);
            if(newCustomerCard == null){
                if(ezshopdb.updateCustomerName(id, newCustomerName)){
                    c.setCustomerName(newCustomerName);
                    customers.replace(c.getId(), c);
                    return true;
                }
                else
                    return false;
            }
            else if(newCustomerCard.isEmpty()){
                if(ezshopdb.updateCustomer(id, newCustomerName, null)){
                    ezshopdb.updateCardAssigned(c.getCustomerCard(), false);
                    c.setCustomerCard(null);
                    c.setCustomerName(newCustomerName);
                    customers.replace(id, c);
                    return true;
                }
                else
                    return false;
            }
            else if(checkCustomerCard(newCustomerCard)){
                if(ezshopdb.updateCustomer(id, newCustomerName, newCustomerCard)){
                    ezshopdb.updateCardAssigned(newCustomerCard, true);
                    c.setCustomerName(newCustomerName);
                    c.setCustomerCard(newCustomerCard);
                    customers.replace(id, c);
                    return true;
                }
                else
                    return false;
            }
            else
                throw new InvalidCustomerCardException();
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        if(id == null || id <= 0)
            throw new InvalidCustomerIdException();

        try {
            if(ezshopdb.deleteCustomer(id)){
                Customer c = customers.get(id);
                ezshopdb.updateCardAssigned(c.getCustomerCard(), false);
                customers.remove(id);
                return true;
            }
            else
                return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        if(id == null || id <= 0)
            throw new InvalidCustomerIdException();

        return customers.get(id);

//        try {
//            return ezshopdb.getCustomer(id);
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        return new ArrayList<>(customers.values());

//        try {
//            return ezshopdb.getAllCustomer();
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    @Override
    public String createCard() throws UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        try {
            String cardCode = ezshopdb.getAssignableLoyaltyCardCode();
            if(cardCode == null) {
                String lastCard = ezshopdb.getLastCard();
                int code;
                if(lastCard == null)
                    code = 1;
                else {
                    code = Integer.parseInt(lastCard);
                    code++;
                }
                cardCode = String.format("%010d", code);
                ezshopdb.insertLoyaltyCard(new LoyaltyCardImpl(cardCode, false));
            }
            return cardCode;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        if(!checkCustomerCard(customerCard))
            throw new InvalidCustomerCardException();

        if(customerId == null || customerId <= 0)
            throw new InvalidCustomerIdException();

        try {
            LoyaltyCardImpl card = ezshopdb.getLoyaltyCard(customerCard);

            if(card == null || card.isAssigned())
                return  false;

            if(ezshopdb.updateCustomerCard(customerId, customerCard)){
                ezshopdb.updateCardAssigned(customerCard, true);
                Customer c = getCustomer(customerId);
                c.setCustomerCard(customerCard);
                customers.put(c.getId(), c);
                return true;
            }
            else
                return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        if(!checkCustomerCard(customerCard))
            throw new InvalidCustomerCardException();

        Customer customer = null;
        for(Customer c : customers.values()){
            if(c.getCustomerCard() != null && c.getCustomerCard().equals(customerCard))
                customer = c;
        }

        try {
//            Customer c = ezshopdb.getCustomerByCard(customerCard);

            if(customer == null)
                return  false;

            if(pointsToBeAdded < 0 && customer.getPoints() < Math.abs(pointsToBeAdded))
                return false;

            if(ezshopdb.updateCustomerPoints(customer.getId(), pointsToBeAdded)){
                customer.setPoints(customer.getPoints() + pointsToBeAdded);
                customers.replace(customer.getId(), customer);
                return true;
            }
            else
                return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        saleTransactionCounter++;
        SaleTransactionImpl st = new SaleTransactionImpl(saleTransactionCounter);
        saleTransactions.put(st.getTicketNumber(), st);
        return st.getTicketNumber();
    }

    //Main method to run SaleTransaction tests properly!
    public int getSaleTransactionsCounter(){
        int counter = saleTransactionCounter;
        return counter;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        if(transactionId == null || transactionId <= 0 ){
            throw new InvalidTransactionIdException();
        }
        if(productCode==null || productCode.isEmpty() || !validBarcode(productCode)  ){
            throw new InvalidProductCodeException();
        } else if(amount < 0){
            throw new InvalidQuantityException();
        }

        ProductType pt = null;
        for(ProductType p : inventory)
            if(p.getBarCode().equals(productCode)) {
                pt = p;
                break;
            }

        SaleTransactionImpl st = (SaleTransactionImpl) saleTransactions.get(transactionId);
        if( pt != null && pt.getQuantity() > amount && st != null && st.getState().equals("OPEN") ){
            double newPrice = st.getPrice();
            List<TicketEntry> ticketList = st.getEntries();
            TicketEntry te = ticketList.stream()
                    .filter(t -> t.getBarCode().equals(productCode))
                    .findAny()
                    .orElse(null);

            for(ProductType p : inventory) {
                if (p.getBarCode().equals(productCode)) {
                    int newQuantity = pt.getQuantity() - amount;
                    p.setQuantity(newQuantity);
                    try {
                        ezshopdb.updateQuantityDB(p.getId(), newQuantity);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                }
            }
            if(te==null){
                te = new TicketEntryImpl(pt.getBarCode(), pt.getProductDescription(), amount, pt.getPricePerUnit(), 0);
                ticketList.add(te);
            } /* if product with same barcode is already in sale */
            else{
                te.setAmount(te.getAmount()+amount);
            }
            newPrice += amount*te.getPricePerUnit();
            st.setPrice(newPrice);
            return true;
        }
        return false;
    }

    @Override
    public boolean addProductToSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException {
        if (!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (RFID == null || RFID.isEmpty() || !validRFID(RFID)){
            throw new InvalidRFIDException();
        }

        Product pt = products.get(RFID);
        SaleTransactionImpl st = (SaleTransactionImpl) saleTransactions.get(transactionId);

        /* if sale transaction exists, is open and product exists */
        if(pt!=null && st!=null && st.getState().equals("OPEN")) {
            try {
                if(addProductToSale(transactionId, pt.getBarcode(), 1)){
                    List<String> rfidList = st.getRFIDList();
                    rfidList.add(RFID);

                    pt.setSaleId(transactionId);
                    return true;
                }
                else{
                    return false;
                }
            } catch (InvalidProductCodeException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }

        if(transactionId == null || transactionId <= 0){
            throw new InvalidTransactionIdException();
        }

        if(productCode==null || productCode.isEmpty() || !validBarcode(productCode) ) {
            throw new InvalidProductCodeException();
        }
        if(amount < 0){
            throw new InvalidQuantityException();
        }

        SaleTransactionImpl st = (SaleTransactionImpl) saleTransactions.get(transactionId);
        double newTotal = st.getPrice();
        List<TicketEntry> ticketList = st.getEntries();
        ProductType pt = null;
        for(ProductType p : inventory)
            if(p.getBarCode().equals(productCode)) {
                pt = p;
                break;
            }

        if( pt != null && st != null && st.getState().equals("OPEN") ){
            TicketEntry ticketToRemove = null;
            for( TicketEntry te : ticketList){
                if( te.getBarCode().equals(productCode) ){
                    if(te.getDiscountRate() == 0.00)
                        newTotal -= amount*te.getPricePerUnit();
                    else
                        newTotal -= amount*te.getPricePerUnit()*te.getDiscountRate();
                    if(te.getAmount() == amount) {
                        ticketToRemove = te;
                    }
                    else if(te.getAmount() > amount)
                        te.setAmount(te.getAmount() - amount);
                    else
                        return false;

                    break;
                }
            }

            if(ticketToRemove != null)
                ticketList.remove(ticketToRemove);

            for(ProductType p : inventory)
                if(p.getBarCode().equals(productCode)) {
                    int newQuantity = pt.getQuantity()+amount;
                    p.setQuantity(newQuantity);
                    try {
                        ezshopdb.updateQuantityDB(p.getId(), newQuantity);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                }
            st.setPrice(newTotal);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException{
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }

        if(RFID==null || RFID.isEmpty() || !validRFID(RFID) ) {
            throw new InvalidRFIDException();
        }

        if(transactionId == null || transactionId <= 0){
            throw new InvalidTransactionIdException();
        }

        Product pt = products.get(RFID);
        SaleTransactionImpl st = (SaleTransactionImpl) saleTransactions.get(transactionId);

        /* if sale transaction exists, is open and product exists */
        if(pt!=null && st!=null && st.getState().equals("OPEN")) {
            try {
                if(deleteProductFromSale(transactionId, pt.getBarcode(), 1)){
                    List<String> rfidList = st.getRFIDList();
                    rfidList.remove(RFID);

                    pt.setSaleId(-1);
                    return true;
                }
                else{
                    return false;
                }
            } catch (InvalidProductCodeException | InvalidQuantityException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        if(transactionId == null || transactionId <= 0){
            throw new InvalidTransactionIdException();
        }
        if(productCode==null || productCode.isEmpty() || !validBarcode(productCode) ) {
            throw new InvalidProductCodeException();
        }
        if(discountRate < 0 || discountRate >= 1.00) {
            throw new InvalidDiscountRateException();
        }
        SaleTransactionImpl st = (SaleTransactionImpl) saleTransactions.get(transactionId);
        List<TicketEntry> ticketList = st.getEntries();
        ProductType pt = inventory.stream()
                .filter(p->p.getBarCode().equals(productCode))
                .findAny()
                .orElse(null);
        double newTotal = st.getPrice();

        if(pt != null &&  st.getState().equals("OPEN") ){
            for(TicketEntry te : ticketList){
                if(te.getBarCode().equals(productCode)){
                    te.setDiscountRate(discountRate);
                    newTotal -= te.getPricePerUnit()*te.getAmount()*discountRate;
                }
            }
            st.setPrice(newTotal);
            return true;
        }
        return false;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        if(transactionId == null || transactionId <= 0 ){
            throw new InvalidTransactionIdException();
        }
        if(discountRate < 0 || discountRate >= 1.00){
            throw new InvalidDiscountRateException();
        }
        SaleTransactionImpl st = (SaleTransactionImpl) saleTransactions.get(transactionId);
        if(st != null && !st.getState().equals("PAYED")){
            double newPrice = st.getPrice();
            newPrice -= st.getPrice()*discountRate;
            st.setDiscountRate(discountRate);
            st.setPrice(newPrice);
            if(st.getState().equals("CLOSED"))
                try {
                    ezshopdb.updateSaleTransaction(st);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            return true;
        }
        return false;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        if(transactionId == null || transactionId <= 0 ){
            throw new InvalidTransactionIdException();
        }
        SaleTransaction st = saleTransactions.get(transactionId);
        if(st != null){
            return (int)Math.floor(st.getPrice()/10);
        }
        return -1;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        if( transactionId == null || transactionId <= 0 ){
            throw new InvalidTransactionIdException();
        }

        SaleTransactionImpl st = (SaleTransactionImpl) saleTransactions.get(transactionId);
        if(st != null && st.getState().equals("OPEN")){
            st.setState("CLOSED");
            try {
                ezshopdb.insertSaleTransaction(st);
                for(TicketEntry te : st.getEntries()){
                    ezshopdb.insertTicketEntry(transactionId, te);
                }
                for(String rfid : st.getRFIDList()){
                    ezshopdb.updateProductSaleId(rfid, st.getTicketNumber());
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        if(saleNumber == null || saleNumber <= 0 ){
            throw new InvalidTransactionIdException();
        }
        SaleTransactionImpl st = (SaleTransactionImpl)saleTransactions.get(saleNumber);
        if(st != null && !st.getState().equals("PAYED")){
            for(TicketEntry te : st.getEntries()){
                ProductType pt = inventory.stream()
                        .filter(p->p.getBarCode().equals(te.getBarCode()))
                        .findAny()
                        .orElse(null);
                int newQuantity = pt.getQuantity()+te.getAmount();
                pt.setQuantity(newQuantity);
                try {
                    ezshopdb.updateQuantityDB(pt.getId(), newQuantity);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            // update of RFID that were in sale
            for(String rfid : st.getRFIDList()){
                Product rfidInSale = products.get(rfid);
                rfidInSale.setSaleId(-1);
                try {
                    ezshopdb.updateProductSaleId(rfid, -1);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            try {
                ezshopdb.deleteSaleTransaction(saleNumber);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            saleTransactions.remove(st.getTicketNumber());
            return true;
        }
        return false;
    }

    public SaleTransaction getSaleTransactionInformations(Integer transactionId){
        return saleTransactions.get(transactionId);
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!checkAuth("Cashier")) {
            throw new UnauthorizedException();
        }
        if(transactionId == null || transactionId <= 0){
            throw new InvalidTransactionIdException();
        }
        SaleTransactionImpl st = (SaleTransactionImpl)saleTransactions.get(transactionId);
        if(st != null && (st.getState().equals("PAYED") || (st.getState().equals("CLOSED"))) ){
            return st;
        }
        return null;
    }

    public TreeMap<Integer, ReturnTransaction> getReturnTransactions() {
        return this.returnTransactions;
    }

    @Override
    public Integer startReturnTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();

        if( transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException();
        }

        SaleTransactionImpl st = (SaleTransactionImpl)saleTransactions.get(transactionId);
        if(st == null || !st.getState().equals("PAYED")){
            return -1;
        }


        ReturnTransaction rt = new ReturnTransaction(++returnTransactionCounter, st.getTicketNumber());
        returnTransactions.put(rt.getId(), rt);

        return rt.getId();
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {

        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();
        if( returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException();

        if( amount <= 0)
            throw new InvalidQuantityException();

        if(productCode==null || productCode.isEmpty() || !validBarcode(productCode))
            throw new InvalidProductCodeException();

        /* Retrieve opened return transaction and product */
        ReturnTransaction rt = returnTransactions.get(returnId);
        ProductType p = inventory.stream().
                filter(pr -> pr.getBarCode().equals(productCode)).
                findAny().
                orElse(null);
        if(rt== null || p == null)
            return false;

        // Retrieve sale transaction of return
        SaleTransaction st = saleTransactions.get(rt.getSaleTransactionId());
        if(st == null)
            return false;

        /* Retrieve products in sale transaction */
        List<TicketEntry> entries = st.getEntries();
        TicketEntry ticketEntry=null;
        /* Retrieve product to be returned */
        for(TicketEntry te : entries){
            if(te.getBarCode().equals(productCode)) {
                ticketEntry = te;
                break;
            }
        }
        if(ticketEntry == null || ticketEntry.getAmount() < amount)
            return false;


        /* Compute new values of product quantity and price to be removed */
        double toBeRemoved = ticketEntry.getPricePerUnit()*amount*
                (1 - ticketEntry.getDiscountRate());

        /* Store information about return transaction */

        /* If this product is already in the entry list of the return transaction */
        List<TicketEntry> returnedProducts = rt.getEntryList();
        TicketEntry returnedProduct = returnedProducts.stream().
                filter(t -> t.getBarCode().equals(productCode))
                .findAny()
                .orElse(null);
        if(returnedProduct == null){
            returnedProduct = new TicketEntryImpl(ticketEntry.getBarCode(),
                    ticketEntry.getProductDescription(), amount, ticketEntry.getPricePerUnit(),
                    ticketEntry.getDiscountRate());
            rt.getEntryList().add(returnedProduct);
        } else {
            // If sold quantity is less or equal than the amount already return plus the new one
            if(returnedProduct.getAmount() + amount > ticketEntry.getAmount())
                return false;
            else{
                /* set new amount */
                returnedProduct.setAmount(returnedProduct.getAmount() + amount);
            }
        }
        rt.setPrice(rt.getPrice() + toBeRemoved);

        return true;
    }

    /**
     * This method adds a product to the return transaction, starting from its RFID
     * This method DOES NOT update the product quantity
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the id of the return transaction
     * @param RFID the RFID of the product to be returned
     *
     * @return  true if the operation is successful
     *          false   if the the product to be returned does not exists,
     *                  if it was not in the transaction,
     *                  if the transaction does not exist
     *
     * @throws InvalidTransactionIdException if the return id is less ther or equal to 0 or if it is null
     * @throws InvalidRFIDException if the RFID is empty, null or invalid
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean returnProductRFID(Integer returnId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException
    {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();
        if( returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException();
        if(RFID==null || RFID.isEmpty() || !validRFID(RFID) ) {
            throw new InvalidRFIDException();
        }

        /* Retrieve opened return transaction and product */
        ReturnTransaction rt = returnTransactions.get(returnId);
        Product p = products.get(RFID);
        ProductType pt = inventory.stream().
                filter(pr -> pr.getBarCode().equals(p.getBarcode())).
                findAny().
                orElse(null);
        if(rt== null || p == null || pt == null)
            return false;

        // Retrieve sale transaction of return
        SaleTransactionImpl st = (SaleTransactionImpl) saleTransactions.get(rt.getSaleTransactionId());
        if(st == null)
            return false;

        /* if product is not in sale transaction */
        if(!st.getRFIDList().contains(RFID))
            return false;

        try {
            if(returnProduct(returnId, p.getBarcode(), 1)){
                List<String> rfidList = rt.getRFIDList();
                rfidList.add(RFID);
                return true;
            }
            else{
                return false;
            }
        } catch (InvalidProductCodeException | InvalidQuantityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the product quantity and the final price of a sale transaction
     *
     * @param st is the sale transaction to update
     * @param rt is the return transaction
     * @param mul is -1 if rt has been ended correctly, thus product quantity must be decreased
     *            is 1 if rt has been deleted, thus product quantity must be increased
     */
    public void updateSaleTransaction(SaleTransactionImpl st, ReturnTransaction rt, int mul) throws UnauthorizedException, InvalidProductCodeException, InvalidProductIdException {

        /* Compute values to update in ST */
        double newPrice = st.getPrice() + mul*rt.getPrice();
        /* Retrieve list of sold products and list of product to return */
        List<TicketEntry> returnedProducts = rt.getEntryList();
        List<TicketEntry> soldProducts = st.getEntries();

        for(TicketEntry rp : returnedProducts){
            for(TicketEntry sp : soldProducts) {
                /* when a product to be returned is found */
                if ( rp.getBarCode().equals(sp.getBarCode()) ){
                    ProductType p = inventory.stream().
                            filter(pr -> pr.getBarCode().equals(rp.getBarCode())).
                            findAny().
                            orElse(null); // never null
                    int newAmount = sp.getAmount() + mul*rp.getAmount();
                    /* quantity is updated both in Sale Transaction and in Product and in DB */
                    sp.setAmount(newAmount);
                    try {
                        ezshopdb.updateTicketEntry(st.getTicketNumber(), sp);
                        int newQuantity = p.getQuantity() - mul*rp.getAmount();
                        p.setQuantity(newQuantity);
                        ezshopdb.updateQuantityDB(p.getId(), newQuantity);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }
        st.setPrice(newPrice);
        try {
            ezshopdb.updateSaleTransaction(st);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * This method removes the RFID of the products returned in rt from st, when rt is committed
     *
     * @param st the Sale Transaction
     * @param rt the Return Transaction
     */
    public void deleteRFIDFromSale(SaleTransactionImpl st, ReturnTransaction rt){

        for(String rfid : rt.getRFIDList()){
            st.getRFIDList().remove(rfid);
            products.get(rfid).setSaleId(-1);
            try {
                ezshopdb.updateProductSaleId(rfid, -1);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * This method adds the RFID of the products returned in rt from st, when rt is deleted
     *
     * @param st the Sale Transaction
     * @param rt the Return Transaction
     */
    public void addRFIDToSale(SaleTransactionImpl st, ReturnTransaction rt){

        for(String rfid : rt.getRFIDList()){
            st.getRFIDList().add(rfid);
            products.get(rfid).setSaleId(st.getTicketNumber());
            try {
                ezshopdb.updateProductSaleId(rfid, st.getTicketNumber());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {

        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();
        if( returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException();

        ReturnTransaction rt = returnTransactions.get(returnId);
        if(rt==null || !rt.getState().equals("OPEN")){
            return false;
        }

        if(!commit)
            // remove return transaction from the system
            returnTransactions.remove(returnId);
        else{
            /* Retrieve sale transaction */
            SaleTransaction st = saleTransactions.get(rt.getSaleTransactionId());
            try {
                updateSaleTransaction((SaleTransactionImpl) st, rt, -1);
                deleteRFIDFromSale((SaleTransactionImpl) st, rt);
            } catch (InvalidProductCodeException | InvalidProductIdException e) {
                e.printStackTrace();
            }
            rt.setState("CLOSED");
            try {
                ezshopdb.insertReturnTransaction(rt);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {

        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();
        if( returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException();
        }

        ReturnTransaction rt = returnTransactions.get(returnId);
        if(rt==null || !rt.getState().equals("CLOSED")){
            return false;
        }

        SaleTransaction st = saleTransactions.get(rt.getSaleTransactionId());
        try {
            updateSaleTransaction((SaleTransactionImpl) st, rt, 1);
            addRFIDToSale((SaleTransactionImpl) st, rt);
            ezshopdb.deleteReturnTransaction(returnId);
        } catch (InvalidProductCodeException | InvalidProductIdException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        returnTransactions.remove(returnId);
        return true;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();
        if( ticketNumber == null || ticketNumber <= 0) {
            throw new InvalidTransactionIdException();
        }
        if( cash <= 0) {
            throw new InvalidPaymentException();
        }
        double money_to_return;

        SaleTransactionImpl Transaction=(SaleTransactionImpl)saleTransactions.get(ticketNumber);
        if(Transaction==null || !Transaction.getState().equals("CLOSED"))
            return -1;
        else {
            if(cash<Transaction.getPrice())
                return -1;
            else {
                money_to_return = cash - Transaction.getPrice();
                try {
                    Integer id = ++balanceOperationCounter;
                    BalanceOperation b = new BalanceOperationImpl(id, LocalDate.now(), Transaction.getPrice(), "CREDIT");
                    balanceOperations.put(id, b);
                    ezshopdb.insertBalanceOperation(b);

                    Transaction.setState("PAYED");
                    Transaction.setBalanceId(balanceOperationCounter);
                    ezshopdb.updateSaleTransaction(Transaction);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    return -1;
                }

                //credit

                return money_to_return;
            }
        }
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if (!checkAuth("Cashier"))
            throw new UnauthorizedException();
        if (ticketNumber == null || ticketNumber <= 0) {
            throw new InvalidTransactionIdException();
        }
        if (creditCard == null ||creditCard.isEmpty() || !checkLuhn(creditCard)) {
            throw new InvalidCreditCardException();
        }

        SaleTransactionImpl transaction = (SaleTransactionImpl) saleTransactions.get(ticketNumber);
        if (transaction != null && transaction.getState().equals("CLOSED")) {
            try {
                String[] values = getCreditCards(creditCard);
                if(!values[0].isEmpty()){
                    if (Double.parseDouble(values[1])>= transaction.getPrice()) {

                        try {
                            Integer id = ++balanceOperationCounter;
                            BalanceOperation b = new BalanceOperationImpl(id, LocalDate.now(), transaction.getPrice(), "CREDIT");
                            balanceOperations.put(id, b);
                            ezshopdb.insertBalanceOperation(b);

                            transaction.setState("PAYED");
                            transaction.setBalanceId(balanceOperationCounter);
                            ezshopdb.updateSaleTransaction(transaction);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                            return false;
                        }

                        return true;
                    }
                }
                else return false;
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

        }
        return false;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();
        if( returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException();
        }
        double money_to_return;

        ReturnTransaction ReturnT=returnTransactions.get(returnId);
        if(ReturnT==null)
            return -1;
        else {
            if(ReturnT.getState().equals("CLOSED")){
                money_to_return = ReturnT.getPrice();
                try {
                    //debit

                    Integer id = ++balanceOperationCounter;
                    BalanceOperation b = new BalanceOperationImpl(id, LocalDate.now(), -(ReturnT.getPrice()), "DEBIT");
                    balanceOperations.put(id, b);
                    ezshopdb.insertBalanceOperation(b);
                    //------------------------------------------------------------------------------
                    ReturnT.setState("PAYED");
                    ReturnT.setBalanceId(balanceOperationCounter);
                    ezshopdb.updateReturnTransaction(ReturnT);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    return -1;
                }


                return money_to_return;
            }
            else return -1;
        }
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if(!checkAuth("Cashier"))
            throw new UnauthorizedException();
        if( returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException();
        }
        if(creditCard==null || creditCard.isEmpty() || !checkLuhn(creditCard)) {
            throw new InvalidCreditCardException();
        }

        ReturnTransaction rtransaction = returnTransactions.get(returnId);
        if (rtransaction != null && rtransaction.getState().equals("CLOSED")) {
            try {
                String[] values = getCreditCards(creditCard);
                if(!values[0].isEmpty()) {
                    double money_to_return = rtransaction.getPrice();

                    try {
                        //debit

                        Integer id = ++balanceOperationCounter;
                        BalanceOperation b = new BalanceOperationImpl(id, LocalDate.now(), -(rtransaction.getPrice()), "DEBIT");
                        balanceOperations.put(id, b);
                        ezshopdb.insertBalanceOperation(b);
                        //------------------------------------------------------------------------------
                        rtransaction.setState("PAYED");
                        rtransaction.setBalanceId(balanceOperationCounter);
                        ezshopdb.updateReturnTransaction(rtransaction);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        return -1;
                    }


                    return money_to_return;
                }
                return -1;
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }

        return -1;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        if( (computeBalance() + toBeAdded) < 0 ){
            return false;
        }

        String t = (toBeAdded >= 0 ? "CREDIT" : "DEBIT");

        Integer id = ++balanceOperationCounter;
        BalanceOperation b = new BalanceOperationImpl(id, LocalDate.now(),
                ( toBeAdded >= 0 ? toBeAdded : -(toBeAdded) ), t);
        balanceOperations.put(id, b);

        try {
            ezshopdb.insertBalanceOperation(b);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {

        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        LocalDate min = (from!=null ? from : LocalDate.MIN);
        LocalDate max = (to!=null ? to : LocalDate.MAX);

        if(min.isAfter(max)){
            LocalDate tmp = min;
            min = max;
            max = tmp;
        }

        LocalDate finalMin = min;
        LocalDate finalMax = max;
        return balanceOperations.values().stream()
                .filter(s-> {return s.getDate().isAfter(finalMin) || s.getDate().isEqual(finalMin);})
                .filter(s-> {return s.getDate().isBefore(finalMax) || s.getDate().isEqual(finalMax);})
                .collect(toList());
    }

    @Override
    public double computeBalance() throws UnauthorizedException {

        if(!checkAuth("ShopManager"))
            throw new UnauthorizedException();

        return balanceOperations.values().stream()
                        .filter(b -> b.getType().equals("CREDIT"))
                        .map(BalanceOperation::getMoney)
                        .reduce(0.0, Double::sum) -
                balanceOperations.values().stream()
                        .filter(b -> b.getType().equals("DEBIT"))
                        .map(BalanceOperation::getMoney)
                        .reduce(0.0, Double::sum);
    }
}
