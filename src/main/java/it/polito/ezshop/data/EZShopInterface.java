package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

import java.time.LocalDate;
import java.util.List;

/* version 1.3 5 june 2021 
changes: added functions to support RFID in each product 
 recordOrderArrivalRFID(), addProductToSaleRFID(), deleteProductFromSaleRFID(), returnProductRFID() 
added InvalidRFIDException
*/ 

/*  version 1.2 29 april 2021
changes:
improved description of reset() function
*/

/*  version 1.1 27 april 2021
changes:
issueReOrder() renamed in issueOrder()

closeSaleTransaction() renamed in endSaleTransaction()

canceled concept of ticket, and consequently:
deleteSaleTicket(ticketNumber) renamed deleteSaleTransaction(transactionId)
Ticket getSaleTicket() changed in   SaleTransaction getSaleTransaction(Integer transactionId)
canceled getTicketByNumber()
startReturnTransaction   receives transactionID and not ticketNumber
receiveCashPayment()    receives transactionID and not ticketNumber
receiveCreditCardPayment()    receives transactionID and not ticketNumber

*/

public interface EZShopInterface {
    /**
     * This method should reset the application to its base state: balance zero, no transacations, no products
     */
    public void reset();

    // -------------------- FR1 ------------------- //

    /**
     * This method creates a new user with given username, password and role. The returned value is a unique identifier
     * for the new user.
     *
     * @param username the username of the new user. This value should be unique and not empty.
     * @param password the password of the new user. This value should not be empty.
     * @param role the role of the new user. This value should not be empty and it should assume
     *             one of the following values : "Administrator", "Cashier", "ShopManager"
     *
     * @return The id of the new user ( > 0 ).
     *          -1 if there is an error while saving the user or if another user with the same username exists
     *
     * @throws InvalidUsernameException If the username has an invalid value (empty or null)
     * @throws InvalidPasswordException If the password has an invalid value (empty or null)
     * @throws InvalidRoleException     If the role has an invalid value (empty, null or not among the set of admissible values)
     */
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException;

    // ------------------- ADMIN ------------------ //
    /**
     * This method deletes the user with given id. It can be invoked only after a user with role "Administrator" is
     * logged in.
     *
     * @param id the user id, this value should not be less than or equal to 0 or null.
     *
     * @return  true if the user was deleted
     *          false if the user cannot be deleted
     *
     * @throws InvalidUserIdException if id is less than or equal to 0 or if it is null.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException;

    /**
     * This method returns the list of all registered users. It can be invoked only after a user with role "Administrator" is
     * logged in.
     *
     * @return  a list of all registered users. If there are no users the list should be empty
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public List<User> getAllUsers() throws UnauthorizedException;

    /**
     * This method returns a User object with given id. It can be invoked only after a user with role "Administrator" is
     * logged in.
     *
     * @param id the id of the user
     *
     * @return  the requested user if it exists, null otherwise
     *
     * @throws InvalidUserIdException if id is less than or equal to zero or if it is null
     * @throws UnauthorizedException  if there is no logged user or if it has not the rights to perform the operation
     */
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException;

    /**
     * This method updates the role of a user with given id. It can be invoked only after a user with role "Administrator" is
     * logged in.
     *
     * @param id the id of the user
     * @param role the new role the user should be assigned to
     *
     * @return true if the update was successful, false if the user does not exist
     *
     * @throws InvalidUserIdException   if the user Id is less than or equal to 0 or if it is null
     * @throws InvalidRoleException     if the new role is empty, null or not among one of the following : {"Administrator", "Cashier", "ShopManager"}
     * @throws UnauthorizedException    if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException;

    // -------------------- LOGIN ----------------- //

    /**
     * This method lets a user with given username and password login into the system
     *
     * @param username the username of the user
     * @param password the password of the user
     *
     * @return an object of class User filled with the logged user's data if login is successful, null otherwise ( wrong credentials or db problems)
     *
     * @throws InvalidUsernameException if the username is empty or null
     * @throws InvalidPasswordException if the password is empty or null
     */
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException;

    /**
     * This method makes a user to logout from the system
     *
     * @return true if the logout is successful, false otherwise (there is no logged user)
     */
    public boolean logout();

    // -------------------- FR3 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //

    /**
     * This method creates a product type and returns its unique identifier. It can be invoked only after a user with role "Administrator"
     * or "ShopManager" is logged in.
     *
     * @param description the description of product to be created
     * @param productCode  the unique barcode of the product
     * @param pricePerUnit the price per single unit of product
     * @param note the notes on the product (if null an empty string should be saved as description)
     *
     * @return The unique identifier of the new product type ( > 0 ).
     *         -1 if there is an error while saving the product type or if it exists a product with the same barcode
     *
     * @throws InvalidProductDescriptionException if the product description is null or empty
     * @throws InvalidProductCodeException if the product code is null or empty, if it is not a number or if it is not a valid barcode
     * @throws InvalidPricePerUnitException if the price per unit si less than or equal to 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note)
            throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException;

    /**
     * This method updates the product id with given barcode and id. It can be invoked only after a user with role "Administrator"
     * or "ShopManager" is logged in.
     *
     * @param id the type of product to be updated
     * @param newDescription the new product type
     * @param newCode the new product code
     * @param newPrice the new product price
     * @param newNote the new product notes
     *
     * @return  true if the update is successful
     *          false if the update is not successful (no products with given product id or another product already has
     *              the same barcode)
     *
     * @throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
     * @throws InvalidProductDescriptionException if the product description is null or empty
     * @throws InvalidProductCodeException if the product code is null or empty, if it is not a number or if it is not a valid barcode
     * @throws InvalidPricePerUnitException if the price per unit si less than or equal to 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote)
            throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException;

    /**
     * This method deletes a product with given product id. It can be invoked only after a user with role "Administrator"
     * or "ShopManager" is logged in.
     *
     * @param id the id of the product to be deleted
     *
     * @return true if the product was deleted, false otherwise
     *
     * @throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException;

    /**
     * This method returns the list of all registered product types. It can be invoked only after a user with role "Administrator",
     * "ShopManager" or "Cashier" is logged in.
     *
     * @return a list containing all saved product types
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public List<ProductType> getAllProductTypes() throws UnauthorizedException;

    /**
     * This method returns a product type with given barcode. It can be invoked only after a user with role "Administrator"
     * or "ShopManager" is logged in.
     *
     * @param barCode the unique barCode of a product
     *
     * @return the product type with given barCode if present, null otherwise
     *
     * @throws InvalidProductCodeException if barCode is not a valid bar code, if is it empty or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException;

    /**
     * This method returns a list of all products with a description containing the string received as parameter. It can be invoked only after a user with role "Administrator"
     * or "ShopManager" is logged in.
     *
     * @param description the description (or part of it) of the products we are searching for.
     *                    Null should be considered as the empty string.
     *
     * @return a list of products containing the requested string in their description
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException;

    // -------------------- FR4 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //

    /**
     * This method updates the quantity of product available in store. <toBeAdded> can be negative but the final updated
     * quantity cannot be negative. The product should have a location assigned to it.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productId the id of the product to be updated
     * @param toBeAdded the quantity to be added. If negative it decrease the available quantity of <toBeAdded> elements.
     *
     * @return  true if the update was successful
     *          false if the product does not exists, if <toBeAdded> is negative and the resulting amount would be
     *          negative too or if the product type has not an assigned location.
     *
     * @throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException;

    /**
     * This method assign a new position to the product with given product id. The position has the following format :
     * <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>
     * The position should be unique (unless it is an empty string, in this case this means that the product type
     * has not an assigned location). If <newPos> is null or empty it should reset the position of given product type.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productId the id of the product to be updated
     * @param newPos the new position the product should be placed to.
     *
     * @return true if the update was successful
     *          false if the product does not exists or if <newPos> is already assigned to another product
     *
     * @throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
     * @throws InvalidLocationException if the product location is in an invalid format (not <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>)
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException;

    /**
     * This method issues an order of <quantity> units of product with given <productCode>, each unit will be payed
     * <pricePerUnit> to the supplier. <pricePerUnit> can differ from the re-selling price of the same product. The
     * product might have no location assigned in this step.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productCode the code of the product that we should order as soon as possible
     * @param quantity the quantity of product that we should order
     * @param pricePerUnit the price to correspond to the supplier (!= than the resale price of the shop) per unit of
     *                     product
     *
     * @return  the id of the order (> 0)
     *          -1 if the product does not exists, if there are problems with the db
     *
     * @throws InvalidProductCodeException if the productCode is not a valid bar code, if it is null or if it is empty
     * @throws InvalidQuantityException if the quantity is less than or equal to 0
     * @throws InvalidPricePerUnitException if the price per unit of product is less than or equal to 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit)
            throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException;

    /**
     * This method directly orders and pays <quantity> units of product with given <productCode>, each unit will be payed
     * <pricePerUnit> to the supplier. <pricePerUnit> can differ from the re-selling price of the same product. The
     * product might have no location assigned in this step.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productCode the code of the product to be ordered
     * @param quantity the quantity of product to be ordered
     * @param pricePerUnit the price to correspond to the supplier (!= than the resale price of the shop) per unit of
     *                     product
     *
     * @return  the id of the order (> 0)
     *          -1 if the product does not exists, if the balance is not enough to satisfy the order, if there are some
     *          problems with the db
     *
     * @throws InvalidProductCodeException if the productCode is not a valid bar code, if it is null or if it is empty
     * @throws InvalidQuantityException if the quantity is less than or equal to 0
     * @throws InvalidPricePerUnitException if the price per unit of product is less than or equal to 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit)
            throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException;

    /**
     * This method change the status the order with given <orderId> into the "PAYED" state. The order should be either
     * issued (in this case the status changes) or payed (in this case the method has no effect).
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param orderId the id of the order to be ORDERED
     *
     * @return  true if the order has been successfully ordered
     *          false if the order does not exist or if it was not in an ISSUED/ORDERED state
     *
     * @throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException;

    /**
     * This method records the arrival of an order with given <orderId>. This method changes the quantity of available product.
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
     */
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException;

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
    public boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException, InvalidRFIDException;
    /**
     * This method return the list of all orders ISSUED, ORDERED and COMLPETED.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @return a list containing all orders
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public List<Order> getAllOrders() throws UnauthorizedException;

    // -------------------- FR5 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //
    // ------------------ CASHIER ----------------- //

    /**
     * This method saves a new customer into the system. The customer's name should be unique.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerName the name of the customer to be registered
     *
     * @return the id (>0) of the new customer if successful, -1 otherwise
     *
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException;

    /**
     * This method updates the data of a customer with given <id>. This method can be used to assign/delete a card to a
     * customer. If <newCustomerCard> has a numeric value than this value will be assigned as new card code, if it is an
     * empty string then any existing card code connected to the customer will be removed and, finally, it it assumes the
     * null value then the card code related to the customer should not be affected from the update. The card code should
     * be unique and should be a string of 10 digits.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer to be updated
     * @param newCustomerName the new name to be assigned
     * @param newCustomerCard the new card code to be assigned. If it is empty it means that the card must be deleted,
     *                        if it is null then we don't want to update the cardNumber
     *
     * @return true if the update is successful
     *          false if the update fails ( cardCode assigned to another user, db unreacheable)
     *
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws InvalidCustomerCardException if the customer card is empty, null or if it is not in a valid format (string with 10 digits)
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException;

    /**
     * This method deletes a customer with given id from the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer to be deleted
     * @return true if the customer was successfully deleted
     *          false if the user does not exists or if we have problems to reach the db
     *
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException;

    /**
     * This method returns a customer with given id.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer
     *
     * @return the customer with given id
     *          null if that user does not exists
     *
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException;

    /**
     * This method returns a list containing all registered users.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the list of all the customers registered
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public List<Customer> getAllCustomers() throws UnauthorizedException;

    /**
     * This method returns a string containing the code of a new assignable card.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the code of a new available card. An empty string if the db is unreachable
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public String createCard() throws UnauthorizedException;

    /**
     * This method assigns a card with given card code to a customer with given identifier. A card with given card code
     * can be assigned to one customer only.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerCard the number of the card to be attached to a customer
     * @param customerId the id of the customer the card should be assigned to
     *
     * @return true if the operation was successful
     *          false if the card is already assigned to another user, if there is no customer with given id, if the db is unreachable
     *
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException;

    /**
     * This method updates the points on a card adding to the number of points available on the card the value assumed by
     * <pointsToBeAdded>. The points on a card should always be greater than or equal to 0.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerCard the card the points should be added to
     * @param pointsToBeAdded the points to be added or subtracted ( this could assume a negative value)
     *
     * @return true if the operation is successful
     *          false   if there is no card with given code,
     *                  if pointsToBeAdded is negative and there were not enough points on that card before this operation,
     *                  if we cannot reach the db.
     *
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException;

    // -------------------- FR6 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //
    // ------------------ CASHIER ----------------- //


    /**
     * This method starts a new sale transaction and returns its unique identifier.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the id of the transaction (greater than or equal to 0)
     */
    public Integer startSaleTransaction() throws UnauthorizedException;

    /**
     * This method adds a product to a sale transaction receiving its barcode, decreasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be added
     * @param amount the quantity of product to be added
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the quantity of product cannot satisfy the request,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidQuantityException if the quantity is less than 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException;
    /**
     * This method adds a product to a sale transaction receiving  its RFID, decreasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param RFID the RFID of the product to be added
     * @return  true if the operation is successful
     *          false   if the RFID does not exist,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidRFIDException if the RFID code is empty, null or invalid
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean addProductToSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException;
      /**
     * This method deletes a product from a sale transaction , receiving its barcode, increasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be deleted
     * @param amount the quantity of product to be deleted
     *
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the quantity of product cannot satisfy the request,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidQuantityException if the quantity is less than 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException;
    /**
     * This method deletes a product from a sale transaction , receiving its RFID, increasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param RFID the RFID of the product to be deleted
     *
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidRFIDException if the RFID is empty, null or invalid
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException;

    /**
     * This method applies a discount rate to all units of a product type with given type in a sale transaction. The
     * discount rate should be greater than or equal to 0 and less than 1.
     * The sale transaction should be started and open.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be discounted
     * @param discountRate the discount rate of the product
     *
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidDiscountRateException if the discount rate is less than 0 or if it greater than or equal to 1.00
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException;

    /**
     * This method applies a discount rate to the whole sale transaction.
     * The discount rate should be greater than or equal to 0 and less than 1.
     * The sale transaction can be either started or closed but not already payed.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param discountRate the discount rate of the sale
     *
     * @return  true if the operation is successful
     *          false if the transaction does not exists
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidDiscountRateException if the discount rate is less than 0 or if it greater than or equal to 1.00
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException;

    /**
     * This method returns the number of points granted by a specific sale transaction.
     * Every 10€ the number of points is increased by 1 (i.e. 19.99€ returns 1 point, 20.00€ returns 2 points).
     * If the transaction with given id does not exist then the number of points returned should be -1.
     * The transaction may be in any state (open, closed, payed).
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     *
     * @return the points of the sale (1 point for each 10€) or -1 if the transaction does not exists
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException;


    /**
     * This method closes an opened transaction. After this operation the
     * transaction is persisted in the system's memory.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     *
     * @return  true    if the transaction was successfully closed
     *          false   if the transaction does not exist,
     *                  if it has already been closed,
     *                  if there was a problem in registering the data
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException;

    /**
     * This method deletes a sale transaction with given unique identifier from the system's data store.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the transaction to be deleted
     *
     * @return  true if the transaction has been successfully deleted,
     *          false   if the transaction doesn't exist,
     *                  if it has been payed,
     *                  if there are some problems with the db
     *
     * @throws InvalidTransactionIdException if the transaction id number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean deleteSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException;

    /**
     * This method returns  a closed sale transaction.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the CLOSED Sale transaction
     *
     * @return the transaction if it is available (transaction closed), null otherwise
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException;

    /**
     * This method starts a new return transaction for units of products that have already been sold and payed.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the transaction
     *
     * @return the id of the return transaction (>= 0), -1 if the transaction is not available.
     *
     * @throws InvalidTransactionIdException if the transactionId  is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public Integer startReturnTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException;

    /**
     * This method adds a product to the return transaction, starting from its barcode
     * The amount of units of product to be returned should not exceed the amount originally sold.
     * This method DOES NOT update the product quantity
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the id of the return transaction
     * @param productCode the bar code of the product to be returned
     * @param amount the amount of product to be returned
     *
     * @return  true if the operation is successful
     *          false   if the the product to be returned does not exists,
     *                  if it was not in the transaction,
     *                  if the amount is higher than the one in the sale transaction,
     *                  if the transaction does not exist
     *
     * @throws InvalidTransactionIdException if the return id is less ther or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidQuantityException if the quantity is less than or equal to 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException;
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
    public boolean returnProductRFID(Integer returnId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException;
    /**
     * This method closes a return transaction. A closed return transaction can be committed (i.e. <commit> = true) thus
     * it increases the product quantity available on the shelves or not (i.e. <commit> = false) thus the whole trasaction
     * is undone.
     * This method updates the transaction status (decreasing the number of units sold by the number of returned one and
     * decreasing the final price).
     * If committed, the return transaction must be persisted in the system's memory.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the id of the transaction
     * @param commit whether we want to commit (True) or rollback(false) the transaction
     *
     * @return  true if the operation is successful
     *          false   if the returnId does not correspond to an active return transaction,
     *                  if there is some problem with the db
     *
     * @throws InvalidTransactionIdException if returnId is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException;

    /**
     * This method deletes a closed return transaction. It affects the quantity of product sold in the connected sale transaction
     * (and consequently its price) and the quantity of product available on the shelves.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the identifier of the return transaction to be deleted
     *
     * @return  true if the transaction has been successfully deleted,
     *          false   if it doesn't exist,
     *                  if it has been payed,
     *                  if there are some problems with the db
     *
     * @throws InvalidTransactionIdException if the transaction id is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException;

    // -------------------- FR7 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //
    // ------------------ CASHIER ----------------- //

    /**
     * This method record the payment of a sale transaction with cash and returns the change (if present).
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the transaction that the customer wants to pay
     * @param cash the cash received by the cashier
     *
     * @return the change (cash - sale price)
     *         -1   if the sale does not exists,
     *              if the cash is not enough,
     *              if there is some problemi with the db
     *
     * @throws InvalidTransactionIdException if the  number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     * @throws InvalidPaymentException if the cash is less than or equal to 0
     */
    public double receiveCashPayment(Integer transactionId, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException;

    /**
     * This method record the payment of a sale with credit card. If the card has not enough money the payment should
     * be refused.
     * The credit card number validity should be checked. It should follow the luhn algorithm.
     * The credit card should be registered in the system.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the sale that the customer wants to pay
     * @param creditCard the credit card number of the customer
     *
     * @return  true if the operation is successful
     *          false   if the sale does not exists,
     *                  if the card has not enough money,
     *                  if the card is not registered,
     *                  if there is some problem with the db connection
     *
     * @throws InvalidTransactionIdException if the sale number is less than or equal to 0 or if it is null
     * @throws InvalidCreditCardException if the credit card number is empty, null or if luhn algorithm does not
     *                                      validate the credit card
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean receiveCreditCardPayment(Integer transactionId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException;

    /**
     * This method record the payment of a closed return transaction with given id. The return value of this method is the
     * amount of money to be returned.
     * This method affects the balance of the application.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the id of the return transaction
     *
     * @return  the money returned to the customer
     *          -1  if the return transaction is not ended,
     *              if it does not exist,
     *              if there is a problem with the db
     *
     * @throws InvalidTransactionIdException if the return id is less than or equal to 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException;

    /**
     * This method record the payment of a return transaction to a credit card.
     * The credit card number validity should be checked. It should follow the luhn algorithm.
     * The credit card should be registered and its balance will be affected.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the id of the return transaction
     * @param creditCard the credit card number of the customer
     *
     * @return  the money returned to the customer
     *          -1  if the return transaction is not ended,
     *              if it does not exist,
     *              if the card is not registered,
     *              if there is a problem with the db
     *
     * @throws InvalidTransactionIdException if the return id is less than or equal to 0
     * @throws InvalidCreditCardException if the credit card number is empty, null or if luhn algorithm does not
     *                                      validate the credit card
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException;

    // -------------------- FR8 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //

    /**
     * This method record a balance update. <toBeAdded> can be both positive and nevative. If positive the balance entry
     * should be recorded as CREDIT, if negative as DEBIT. The final balance after this operation should always be
     * positive.
     * It can be invoked only after a user with role "Administrator", "ShopManager" is logged in.
     *
     * @param toBeAdded the amount of money (positive or negative) to be added to the current balance. If this value
     *                  is >= 0 than it should be considered as a CREDIT, if it is < 0 as a DEBIT
     *
     * @return  true if the balance has been successfully updated
     *          false if toBeAdded + currentBalance < 0.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException;

    /**
     * This method returns a list of all the balance operations (CREDIT,DEBIT,ORDER,SALE,RETURN) performed between two
     * given dates.
     * This method should understand if a user exchanges the order of the dates and act consequently to correct
     * them.
     * Both <from> and <to> are included in the range of dates and might be null. This means the absence of one (or
     * both) temporal constraints.
     *
     *
     * @param from the start date : if null it means that there should be no constraint on the start date
     * @param to the end date : if null it means that there should be no constraint on the end date
     *
     * @return All the operations on the balance whose date is <= to and >= from
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException;

    /**
     * This method returns the actual balance of the system.
     *
     * @return the value of the current balance
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public double computeBalance() throws UnauthorizedException;

}
