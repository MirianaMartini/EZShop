# Integration and API Test Documentation

Authors: Biasi Cristina 281936, Cielo Fabio 292464, Guarnieri Enea 292561, Martini Miriana 283238

Date: 26/05/2021

Version: 1.0

Version: 1.1 - 04/06/2021<br/>Minor changes in Integration approach, Tests tables and Coverage of scenarios and FR

Version 2.0 - 12/06/2021
Changes in document due to the implementation of Change Request - Add RFID on each product


# Contents

- [Dependency graph](#dependency graph)

- [Integration approach](#integration)

- [Tests](#tests)

- [Scenarios](#scenarios)

- [Coverage of scenarios and FR](#scenario-coverage)
- [Coverage of non-functional requirements](#nfr-coverage)



# Dependency graph 

![Dependency graph](./images/DependencyGraph.png)
     
# Integration approach

The integration sequence adopted is bottom-up. In the Unit Testing phase we tested classes with no significant dependencies (User, Customer, Order, ProductType, Product, BalanceOperation, LoyaltyCard, SaleTransaction, ReturnTransaction, TicketEntry); then we tested the integration of EZShopDB class with these classes; finally we tested the EZShop class and its methods, and their dependencies.

#  Tests

 

## Step 1
| Classes  | JUnit test cases |
|--|--|
| UserImpl | *package UnitTest*<br/> - TestUser.testUser()|
| CustomerImpl | *package UnitTest*<br/> - TestCustomer.testCustomer()|
| LoyaltyCardImpl | *package UnitTest*<br/> - TestLoyaltyCard.testLoyaltyCard()|
| ProductTypeImpl | *package UnitTest*<br/> - TestProductType.testProductType()|
| OrderImpl | *package UnitTest*<br/> - TestOrder.testOrder()|
| SaleTransactionImpl |*package UnitTest*<br/> - TestSaleTransaction.testSaleTransaction()|
| TicketEntryImpl | *package UnitTest*<br/> - TestTicketEntry.testTicketEntry()|
| ReturnTransaction | *package UnitTest*<br/> - TestReturnTransaction.testReturnTransaction() |
| BalanceOperationImpl | *package UnitTest*<br/> - TestBalanceOperation.testBalanceOperation() |
| Product | *package UnitTest*<br/> - TestProduct.testProduct() |
| EZShop.validBarcode | *package UnitTest* <br/>- TestValidBarcode.testNullBarcode()<br/>- TestValidBarcode.testBarcodeLength()<br/>- TestValidBarcode.testWellFormedBarcode()<br/>- TestValidBarcode.testValidBarcode() |
| EZShop.checkCustomerCard | *package UnitTest*<br />- TestLoyaltyCardCode.testNull()<br />- TestLoyaltyCardCode.testEmpty()<br />- TestLoyaltyCardCode.testShortMixed()<br />- TestLoyaltyCardCode.testLongMixed()<br />- TestLoyaltyCardCode.testMixed()<br />- TestLoyaltyCardCode.testCorrect() |
| EZShop.CheckLuhn<br/>EZShop.getCreditCards | *package UnitTest*<br/>- TestCreditCard.testNullLuhn() <br/>- TestCreditCard.TestLuhnLength <br/>- TestCreditCard.testWellFormedLuhn() <br/>- TestCreditCard.testValidLuhn() <br/>- TestCreditCard.testNullGetCreditCard() <br/>- TestCreditCard.testEmptyGetCreditCard()<br/>- TestCreditCard.testCreditCardNotFound()<br/>- TestCreditCard.testCreditCardFound() |
| EZShop.validPosition |  *package UnitTest*<br/>- TestPosition.testPositionNumberOfFields()<br/>- TestPosition.testPositionSeparator()<br/>- TestPosition.testPositionFieldType()<br/>- TestPosition.testPositionFieldSign()<br/>- TestPosition.testPositionWellFormed()<br/>- TestPosition.testValidPosition() |
| EZShop.validRFID | *package UnitTest*<br/>- TestValidRFID.testNullRFID()<br/>- TestValidRFID.testRFIDLength()<br/>- TestValidRFID.testWellFormedRFID()<br/>- TestValidRFID.testPositiveRFID()<br/> |


## Step 2
| Classes  | JUnit test cases |
|--|--|
| EZShopDB + ProductType | *package IntegrationTest*<br/>- TestEZShopDB.testGetProductsEmpty()<br/>- TestEZShopDB.testInsertAndGetOneProductType()<br/>- TestEZShopDB.TestInsertAndGetTwoProductType()<br/>- TestEZShopDB.testUpdateProductType()<br/>- TestEZShopDB.testDeleteProductType()<br/>- TestEZShopDB.testUpdateQuantityProd()<br/>- TestEZShopDB.testUpdatePositionProd()|
| EZShopDB + Order | *package IntegrationTest*<br/>- TestEZShopDB.testDeleteOrders()<br/>- TestEZShopDB.testInsertAndGetOneOrder()<br/>- TestEZShopDB.testInsertOrderThrowsException()<br/>- TestEZShopDB.testInsertAndGetTwoOrders()<br/>- TestEZShopDB.testUpdateOrderThrowsException()<br/>- TestEZShopDB.testGetOrdersEmpty()<br/>- TestEZShopDB.testUpdateOrder() |
| EZShopDB + BalanceOperation | *package IntegrationTest*<br/>- TestEZShopDB.testDeleteBalanceOperations()<br/>- TestEZShopDB.testInsertAndGetOneBalanceOperation()<br/>- TestEZShopDB.testInsertBalanceOperationThrowsException()<br/>- TestEZShopDB.testInsertAndGetTwoBalanceOperations()<br/>- TestEZShopDB.testGetBalanceOperationsEmpty() |
| EZShopDB + ReturnTransaction | *package IntegrationTest*<br/>- TestEZShopDB.TestEZShopDB.testDeleteReturnTransactions()<br/>- TestEZShopDB.testDeleteReturnTransaction()<br/>- TestEZShopDB.testDeleteReturnTransactionThrowsException()<br/>- TestEZShopDB.testInsertAndGetOneReturnTransaction()<br/>- TestEZShopDB.testInsertReturnTransactionThrowsException()<br/>- TestEZShopDB.testInsertAndGetTwoReturnTransactions()<br/>- TestEZShopDB.testGetReturnTransactionsEmpty()<br/>- TestEZShopDB.testUpdateReturnTransactionClosed()<br/>- TestEZShopDB.testUpdateReturnTransactionPayed()<br/>- TestEZShopDB.testUpdateReturnTransactionThrowsException() |
| EZShopDB + TicketEntry<br/>(for Return Transaction) | *package IntegrationTest*<br/>- TestEZShopDB.testDeleteAllReturnedProducts()<br/>- TestEZShopDB.testDeleteReturnedProduct()<br/>- TestEZShopDB.testDeleteReturnedProductThrowsException()<br/>- TestEZShopDB.testDeleteReturnedProductsInReturn()<br/> - TestEZShopDB.testDeleteReturnedProductsInReturnThrowsException()<br/>- TestEZShopDB.testInsertAndGetOneReturnedProduct()<br/>- TestEZShopDB.testInsertReturnedProductThrowsException()<br/>- TestEZShopDB.testInsertAndGetTwoReturnedProducts()<br/>- TestEZShopDB.testGetReturnedProductsEmpty()<br/>- TestEZShopDB.testUpdateReturnedProductThrowsException() |
| EzShopDB + User | *package IntegrationTest*<br/>- TestEZShopDB.testInsertAndGetUsers()<br />- TestEZShopDB.deleteUser()<br />- TestEZShopDB.updateUser() |
| EzShopDB + Customer | *package IntegrationTest*<br/>- TestEZShopDB.testInsertAndGetCustomer()<br />- TestEZShopDB.deleteCustomer()<br />- TestEZShopDB.testUpdateCustomerPoints()<br />- TestEZShopDB.testUpdateCustomerName()<br />- TestEZShopDB.testUpdateCustomerCard()<br />- TestEZShopDB.testUpdateCustomer() |
| EzShopDB + LoyaltyCard | *package IntegrationTest*<br/>- TestEZShopDB.testInsertAndGetLoyaltyCard()<br />- TestEZShopDB.testUpdateCardAssigned()<br />- TestEZShopDB.getLastCard() |
| EZShopDB + User | *package IntegrationTest*<br/>- TestCheckAuth.testNull()<br />- TestCheckAuth.testAdministrator()<br />- TestCheckAuth.testShopManager()<br />- TestCheckAuth.testCashier()<br />- TestCheckAuth.testWrong() |
| EZShopDB + SaleTransaction | *package IntegrationTest* <br/>- TestEZShopDB.testGetSaleTransactions() <br/>- TestEZShopDB.testInsertAndGetSaleTransaction() <br/>- TestEZShopDB.testInsertSaleTransactionThrowsException()<br/>- TestEZShopDB.testGetSaleTransactionNoRecordFound() <br/>- TestEZShopDB.testUpdateSaleTransaction()  <br/>- TestEZShopDB.testDeleteSaleTransaction()|
| EZShopDB + TicketEntry<br/>(for Sale Transaction) | *package IntegrationTest* <br/>- TestEZShopDB.testInsertAndGetTicketEntries() |
| EZShopDB + Product | *package IntegrationTest* <br />- TestEZShopDB.testInsertGetProduct()<br />- TestEZShopDB.testDeleteProduct()<br />- TestEZShopDB.testDeleteAllProduct()<br />\- TestEZShopDB.testUpdateProduct() |
| EZShop + EZShopDB | *package IntegrationTest*<br/>- TestReset.testReset() |

## Step 3 

   

| Classes  | JUnit test cases |
|--|--|
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest*<br/>- TestIntegrationOrder.testIssueOrderThrowsException()<br/>- TestIntegrationOrder.testIssueOrderFails()<br/>- TestIntegrationOrder.testIssueOrderValidProduct()<br/>- TestIntegrationOrder.testPayOrderForThrowsException()<br/>- TestIntegrationOrder.testPayOrderForFails()<br/>- TestIntegrationOrder.testPayOrderForValid()<br/>- TestIntegrationOrder.testPayOrderThrowsException()<br/>- TestIntegrationOrder.testPayOrderFails()<br/> - TestIntegrationOrder.testPayOrderValid()<br/>- TestIntegrationOrder.testRecordOrderArrival()|
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest* <br/>- TestIntegrationBalanceOperation.testRecordBalanceUpdateThrowsException()<br/>- TestIntegrationBalanceOperation.testRecordBalanceUpdateFails()<br/>- TestIntegrationBalanceOperation.testRecordBalanceUpdate()<br/>- TestIntegrationBalanceOperation.testComputeBalanceThrowsException()|
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest*<br/>- TestIntegrationReturnTransaction.testStartReturnTransactionThrowsException()<br/>- TestIntegrationReturnTransaction.testStartReturnTransactionFails()<br/>- TestIntegrationReturnTransaction.testStartReturnTransaction()<br/>- TestIntegrationReturnTransaction.testReturnProductThrowsException()<br/>- TestIntegrationReturnTransaction.testReturnProductFails()<br/>- TestIntegrationReturnTransaction.testReturnProduct()<br/>- TestIntegrationReturnTransaction.testEndReturnTransactionThrowsException()<br/>- TestIntegrationReturnTransaction.testEndReturnTransactionFails()<br/>- TestIntegrationReturnTransaction.testEndReturnTransaction()<br/>- TestIntegrationReturnTransaction.testEndReturnTransactionNoCommit()<br/>- TestIntegrationReturnTransaction.testDeleteReturnTransactionThrowsException()<br/>- TestIntegrationReturnTransaction.testDeleteReturnTransactionFails()<br/>- TestIntegrationReturnTransaction.testDeleteReturnTransaction()<br/>- TestIntegrationReturnTransaction.testReturnTransactionCreditCard()<br/>- TestIntegrationReturnTransaction.testReturnTransactionCashPayment()|
|EZShop + EZShopDB + Unit Classes |*package IntegrationTest* <br/>- TestProductType.testCreateProduct()<br/>- TestProductType.testUpdateProduct()<br/>- TestProductType.testDeleteProduct()<br/>- TestProductType.testGetAllProducts()<br/>- TestProductType.testGetProductByBarcode()<br/>- TestProductType.testGetProductByDescription()<br/>- TestProductType.testUpdateProductQuantity()<br/>- TestProductType.testUpdateProductPosition()|
|EZShop + EZShopDB + Unit Classes |*package IntegrationTest*<br/>- TestReceivePayment.testReceiveCashPayment()<br/>- TestReceivePayment.testReceiveCreditCardPayment()<br/>- TestReturnPayment.testCompleteSaleTransaction()<br/>- TestReturnPayment.testAbortPaymentAndDeleteSaleTransaction()|
|EZShop + EZShopDB + Unit Classes |*package IntegrationTest*<br/>- TestReturnPayment.testReturnCashPayment() <br/>- TestReturnPayment.testReturnCreditCardPayment()|
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest*<br />- TestIntegrationUser.testCreateUser()<br />- TestIntegrationUser.testDeleteUser()<br />- TestIntegrationUser. testGetAllUsers()<br />- TestIntegrationUser.testGetSingleUser()<br />- TestIntegrationUser.testUpdateUserRights()<br />- TestIntegrationUser.testLogin()<br />- TestIntegrationUser.testLogout() |
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest*<br/>- TestIntegrationCustomer.testCreateCustomer()<br />- TestIntegrationCustomer.updateCustomer()<br />- TestIntegrationCustomer.testDeleteUser()<br />- TestIntegrationCustomer.testGetSingleCustomer()<br />- TestIntegrationCustomer.testAllCustomers()<br />- TestIntegrationCustomer.testCreateCard()<br />- TestIntegrationCustomer.testAttachCardToCustomer()<br />- TestIntegrationCustomer.testModifyPointsOnCard() |
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest*<br/>- TestIntegrationSaleTransaction.testStartSaleTransaction()<br/>- TestIntegrationSaleTransaction.testApplyDiscountRateToOpenedSale() <br/>- TestIntegrationSaleTransaction.testComputePointsForClosedSale()<br/>- TestIntegrationSaleTransaction.testAddProductToSale() <br/>- TestIntegrationSaleTransaction.testDeleteProductFromSale() <br/>- TestIntegrationSaleTransaction.testApplyDiscountRateToProduct() <br/>- TestIntegrationSaleTransaction.testApplyDiscountRateToOpenedSale() <br/>- TestIntegrationSaleTransaction.testComputePointsForOpenedSale() <br/> |
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest*<br/>-TestOrderRFID.testOrderRFID() |
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest*<br/>-TestReturnTransactionRFID.testReturnTransactionRFID() |
| EZShop + EZShopDB + Unit Classes | *package IntegrationTest*<br/>-TestSaleTransactionRFID.testSaleTransactionRFID() |


# Scenarios

## Scenario 1.4- UC1
| Scenario |  Delete product type X |
| ------------- |:-------------:|
|  Precondition     |  User C exists and is logged in|
|| Product type X exists |
|  Post condition     |  X deleted from the system |
| Step#        | Description  |
|  1     |  C searches X via bar code |
|  2     |  C selects X's record |
|3 | X deleted from the system|

## Scenario 2.4 - UC2

| Scenario       |                        Show all users                        |
| -------------- | :----------------------------------------------------------: |
| Precondition   |                  Administrator Admin exist                   |
| Post condition |                     Admin is logged out                      |
| Step#          |                         Description                          |
| 1              |         Admin insert username and password and login         |
| 2              |                Admin show a list of all users                |
| 3              | System checks if Admin is logged and have correct access rights and displat the list |
| 4              |                        Admin log out                         |

## Scenario 3.4 -  UC3

| Scenario 3.4 |  Order of product type X directly payed |
| ------------- |:-------------:|
|  Precondition     | ShopManager S exists and is logged in |
| | Product type X exists |
| | Balance >= Order.units * Order.pricePerUnit |
|  Post condition     | Order O exists and is in PAYED state  |
| | Balance -= Order.units * Order.pricePerUnit |
| | X.units not changed |
| Step#        | Description  |
|  1    | S creates order O |
|  2    |  S fills  quantity of product to be ordered and the price per unit |
|    3 | S register payment done for O |
|  4    |  O is recorded in the system in PAYED state |

## Scenario 4.5 - UC4

| Scenario       |           Modify Points            |
| -------------- | :--------------------------------: |
| Precondition   | Account U for Customer Cu existing |
|                |    Loyalty card L attached to U    |
| Post condition |      Customer points updated       |
| Step#          |            Description             |
| 1              |   User selects customer record U   |
| 2              |    User update Customer points     |

## Scenario 8.3 - UC8

| Scenario 8.3 |  Return transaction of product type X deleted |
| ------------- |:-------------:|
|  Precondition     | Cashier C exists and is logged in |
| | Product Type X exists |
| | Ticket T exists and has at least N units of X |
| | Ticket T was paid with credit card |
| | Return transaction of M units of product X is closed but not payed |
|  Post condition     | Return transaction is deleted from the system  |
| | X.quantity in inventory -= N |
| Step#        | Description  |
|  1    |  C inserts T.ticketNumber |
|  2    |  Return transaction starts |
|  3    |  C reads bar code of X |
|  4    |  C adds N units of X to the return transaction |
|  5    |  X available quantity is increased by N |
|  6    |  Return transaction is deleted |
|  7   |  X available quantity is decreased by N |




# Coverage of Scenarios and FR




| Scenario ID | Functional Requirements covered | JUnit  Test(s) |
| ----------- | ------------------------------- | ----------- |
|  1.1         | FR3.1, FR4.2                   |    *package IntegrationTest* <br/>- TestProductType.testUpdateProductPosition()        |
|  1.2         | FR3.4, FR4.3                             |    *package IntegrationTest* <br/>- TestProductType.testUpdateProductPosition()|
| 1.3         |   FR3.4, FR3.1                          |       *package IntegrationTest* <br/>- TestProductType.testGetProductByBarcode()|
|1.4         |   FR3.2                          |       *package IntegrationTest* <br/>- TestProductType.testDeleteProduct()|
|  2.1       | FR1.1                           | *package IntegrationTest*<br/>- TestIntegrationUser.testCreateUser() |
|  2.2       | FR1.2, FR1.4         | *package IntegrationTest*<br/>- TestIntegrationUser.testDeleteUser()<br />- TestIntegrationUser.testGetSingleUser() |
| 2.3      | FR1.1, FR1.4 | *package IntegrationTest*<br/>- TestIntegrationUser.testUpdateUserRights()<br />- TestIntegrationUser.testGetSingleUser() |
| 2.4 | FR1.5, FR1.3 | *package IntegrationTest*<br/>- TestCheckAuth.testAdministrator()<br />- TestIntegrationUser.testLogin()<br />- TestIntegrationUser.testLogout()<br />- TestIntegrationUser.testGetAllUsers() |
| 3.1        |  FR4.3    | *package IntegrationTest*<br/>- TestIntegrationOrder.testIssueOrderValidProduct() |
| 3.2        |  FR4.5, FR8.1    | *package IntegrationTest*<br/>- TestIntegrationOrder.testPayOrderValid() |
| 3.3        |   FR4.6   | *package IntegrationTest*<br/>- TestIntegrationOrder.testRecordOrderArrival() |
| 3.4        |   FR4.4, FR8.1   | *package IntegrationTest*<br/>- TestIntegrationOrder.testPayOrderForValid() |
| 4.1       | FR5.1 | *package IntegrationTest*<br/>- TestIntegrationCustomer.testCreateCustomer()<br />- TestIntegrationCustomer.updateCustomer() |
| 4.2      | FR5.6 | *package IntegrationTest*<br/>- TestIntegrationCustomer.testCreateCard()<br />- TestIntegrationCustomer.testAttachCardToCustomer() |
| 4.3 | FR5.1, 5.3 | *package IntegrationTest*<br/>- TestIntegrationCustomer.testGetSingleCustomer()<br />- TestIntegrationCustomer.testUpdateCustomer() |
| 4.4 | FR5.3, 5.4 | *package IntegrationTest*<br/>- TestIntegrationCustomer.testUpdateCustomer() |
| 4.5 | FR5.7 | *package IntegrationTest*<br/>- TestIntegrationCustomer.testModifyPointsOnCard() |
| 6.1 | FR4.1, FR6.1, FR6.2, FR6.10, FR6.11, FR7.1, FR8.2 | *package IntegrationTest*<br/>- TestReceivePayment.testReceiveCashPayment() |
| 6.2 | FR4.1, FR6.1, FR6.2, FR6.4, FR6.5, FR6.10, FR7.1, FR8.2 | *package IntegrationTest*<br/>- TestReceivePayment.testCompleteSaleTransaction() |
| 6.3 | FR4.1, FR6.1, FR6.2, FR6.4, FR6.5, FR6.10, FR7.1, FR8.2 | *package IntegrationTest*<br/>- TestReceivePayment.testCompleteSaleTransaction() |
| 6.4 | FR4.1, FR5.7, FR6.1, FR6.2, FR6.4, FR6.5, FR6.6 FR6.10, FR7.1, FR8.2 | *package IntegrationTest*<br/>- TestReceivePayment.testCompleteSaleTransaction()<br/>- TestIntegrationCustomer.testModifyPointsOnCard() |
| 6.5 | FR4.1, FR6.1, FR6.2, FR6.10, FR7.1 | *package IntegrationTest*<br/>- TestReceivePayment.testAbortPaymentAndDeleteSaleTransaction() |
| 6.6 | FR4.1, FR6.1, FR6.2, FR6.10, FR6.11, FR7.1, FR8.2 | *package IntegrationTest*<br/>- TestReceivePayment.testReceiveCashPayment |
| 7.1         |     FR7.2                       |   *package IntegrationTest*<br/>- TestReceivePayment.testReceiveCreditCardPayment()           |
| 7.2         |     FR7.2                       |   *package IntegrationTest*<br/>- TestReceivePayment.testReceiveCreditCardPayment()           |
| 7.3         |   FR7.2                       |  *package IntegrationTest*<br/>- TestReceivePayment.testReceiveCreditCardPayment()           |
| 7.4         |      FR7.1                           |   *package IntegrationTest*<br/>- TestReceivePayment.testReceiveCashPayment()         |
| 8.1        |  FR6.12, FR6.13, FR6.14, FR7.4, FR8.1     | *package IntegrationTest*<br/>- TestIntegrationReturnTransaction.testReturnTransactionCreditCard() |
| 8.2        |  FR6.12, FR6.13, FR6.14, FR7.3, FR8.1    | *package IntegrationTest*<br/>- TestIntegrationReturnTransaction.testReturnTransactionCashPayment() |
| 8.3        |  FR6.12, FR6.13, FR6.14, FR6.15, FR8.1    | *package IntegrationTest*<br/>- TestIntegrationReturnTransaction.testDeleteReturnTransaction() |
| 9.1        |   FR8.3   | *package IntegrationTest*<br/>-TestIntegrationBalanceOperation.testRecordBalanceUpdate() |



# Coverage of Non Functional Requirements




| Non Functional Requirement | Test name |
| -------------------------- | --------- |
|              NFR4              |      *package UnitTest*<br/>- TestBarcode.testNullBarcode()<br/>- TestBarcode.testBarcodeLenght()<br/>- TestBarcode.testWellFormedBarcode()<br/>- TestBarcode.testValidBarcode()     |
|  NFR5  |  *package UnitTest*<br/>- TestCreditCard.testNullLuhn()<br/>- TestCreditCard.testLuhnLength()<br/>- TestCreditCard.testWellFormedLuhn()<br/>- TestCreditCard.testValidLuhn()  |
|  NFR6  | *package UnitTest*<br/>- TestLoyaltyCardCode.testNull()<br />- TestLoyaltyCardCode.testEmpty()<br />- TestLoyaltyCardCode.testShortMixed()<br />- TestLoyaltyCardCode.testLongMixed()<br />- TestLoyaltyCardCode.testMixed()<br />- TestLoyaltyCardCode.testCorrect() |
