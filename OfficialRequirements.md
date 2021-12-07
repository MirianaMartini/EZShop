# Requirements Document - EZ Shop 

Authors: Vittorio Di Leo, Maurizio Morisio 

Date: 21 April 2021

Version: 1.0

# Contents

- [Abstract](#abstract)
- [Stakeholders](#stakeholders)
- [Context Diagram and interfaces](#context-diagram-and-interfaces)
	+ [Context Diagram](#context-diagram)
	+ [Interfaces](#interfaces) 
	
- [Stories and personas](#stories-and-personas)
- [Functional and non functional requirements](#functional-and-non-functional-requirements)
	+ [Functional Requirements](#functional-requirements)
	+ [Non functional requirements](#non-functional-requirements)
- [Use case diagram and use cases](#use-case-diagram-and-use-cases)
	+ [Use case diagram](#use-case-diagram)
	+ [Use cases](#use-cases)
    	+ [Relevant scenarios](#relevant-scenarios)
- [Glossary](#glossary)
- [System design](#system-design)
- [Deployment diagram](#deployment-diagram)


# Abstract
Small shops require a simple application to support the owner or manager. A small shop (ex a food shop) occupies 50-200 square meters, sells 500-2000 different item types, 
has one cash register.

EZShop is a software application to
- manage sales
- manage inventory
- manage supply orders
- manage customers
- support accounting


# Stakeholders

| Stakeholder name  | Description | 
| ----------------- |:-----------|
| Cashier | Handles sales and returns  |
| Shop manager | Monitors inventory, does accounting, monitors profits and losses, manage supply orders |
| Customer  | Buys items from the shop |
|      Administrator             |   Installs the application, mantains it, defines users, assign privileges |
| Credit Card Circuit | Provides payment services |
| Supplier | Receives orders, provides products |

# Context Diagram and interfaces

## Context Diagram

```plantuml
top to bottom direction
actor Administrator as a
actor Cashier as ch
actor CreditCardCircuit as cc
actor ShopManager as mngr
actor Supplier as sp
a -up-|> mngr
mngr -up-|> ch
ch -> (EZShop)
 (EZShop) --> cc
 (EZShop) --> sp
```

## Interfaces

| Actor | Logical Interface | Physical Interface  |
| ------------- |:-------------|:-----|
| Cashier | GUI | Screen keyboard mouse on PC|
| Shop manager | GUI | Screen keyboard mouse on PC|
| Administrator | GUI | Screen keyboard mouse on PC  |
| CreditCardCircuit|API, see https://www.programmableweb.com/api/visa-merchant-benchmark-rest-api-v1 | Internet link|
| Supplier | send receive email (email gateway) | Internet link|




# Functional and non functional requirements

## Functional Requirements


| ID        | Description  |
| ------------- |:-------------| 
|  FR1     |  Manage users and rights (users are Administrator, ShopManager, Cashier) |
| FR1.1    | Define a new user, or modify an existing user |
| FR1.2     | Delete a user |
| FR1.3     |    List all users   |
| FR1.4    |    Search a user   |
| FR1.5  |  Manage rights. Authorize access to functions to specific actors according to access rights|
| FR3   | Manage product catalog |
| FR3.1    | Define a new product type, or modify an existing product type|
| FR3.2     | Delete a product type |
| FR3.3     |    List all products types |
| FR3.4   | Search a product type (by bar code, by description) |
| FR4 | Manage inventory |
| FR4.1 | Modify quantity available for a product type |
| FR4.2 | Modify position for a product type |
| FR4.3 | Issue a reorder warning for a product type|
| FR4.4 | Send and pay an order for a product type |
| FR4.5 | Pay an issued reorder warning |
| FR4.6 | Record order arrival |
| FR4.7 | List all orders (issued, payed, completed) |
| FR 5 | Manage customers and cards |
| FR5.1 | Define or modify a customer |
| FR5.2 | Delete a customer  |
| FR5.3 | Search a customer  |
| FR5.4 | List  all customers  |
| FR5.5 | Create a card  |
| FR5.6 | Attach card to a customer  |
| FR5.7 | Modify points on a card   |
| FR6 | Manage a sale transaction |
| FR6.1 | Start a sale  |
| FR6.2 | Add  a product to a sale  |
| FR6.3 | Delete a product from a sale  |
| FR6.4 | Apply discount rate to a sale  |
| FR6.5 | Apply discount rate to a product type  |
| FR6.6 | Compute points for a sale |
| FR6.7 | Read bar code on product |
| FR6.8 | Print sale ticket |
| FR6.9 | Get sale ticket from ticket number |
| FR6.10 | Close  a sale transaction  |
| FR6.11 | Rollback or commit a closed sale transaction  |
| FR6.12 | Start  a return transaction  |
| FR6.13 | Return a product listed in a sale ticket |
| FR6.14 | Close  a return transaction  |
| FR6.15 | Rollback or commit a closed return transaction  |
| FR7 | Manage payment |
| FR7.1 | Receive payment cash|
| FR7.2 | Receive payment credit card|
| FR7.3 | Return payment cash|
| FR7.4 | Return payment credit card|
| FR8 | Accounting |
| FR8.1 | Record debit |
| FR8.2 | Record credit |
| FR8.3 | Show credits and debits over a period of time |
| FR8.4 | Compute balance  |

### Access right, actor vs function

| Function | Administrator | Shop Manager | Cashier |
| -------- | ----- | ------------ | ------- |
| FR1 | yes | no | no |
| FR3| yes  | yes |  no |
| FR3.3| yes  | yes |  yes |
| FR4 | yes | yes | no |
| FR5 | yes  | yes|  yes|
|FR6 | yes  | yes |  yes|
|FR7 | yes | yes| yes |
|FR8 | yes | yes|  no |



## Non Functional Requirements
| ID        | Type        | Description  | Refers to |
| ------------- |:-------------:| :-----| -----:|
|  NFR1     | Usability | Application should be used with no specific training for the users | All FR |
|  NFR2     | Performance | All functions should complete in < 0.5 sec  | All FR |
|  NFR3     |                     Privacy                     | The data of a customer should not be disclosed outside the application | All FR |
|  NFR4     | Domain | The barcode number related to a product type should be a string of digits of either 12, 13 or 14 numbers validated following this algorithm  https://www.gs1.org/services/how-calculate-check-digit-manually | All FR |
|  NFR5     | Domain | The credit cards numbers should be validated through the Luhn algorithm. | FR7 |
|  NFR6     | Domain | The customer's card should be a string of 10 digits. | FR5 |







# Use case diagram and use cases


## Use case diagram

```plantuml
left to right direction
actor Administrator as a
actor Cashier as ch
actor CreditCardCircuit as cc
actor ShopManager as mngr
a -up-|> mngr
mngr -up-|> ch

a --> (Manage users and rights)
ch --> (Manage sale transaction)
ch --> (Manage return transaction)
ch --> (Manage customers and cards)
mngr --> (Manage inventory and orders)
(Payment by credit card) --> cc
(Return by credit card) ---> cc

(EZShop) .> (Authenticate authorize) :include
(EZShop) .> (Manage users and rights) :include
(EZShop) .> (Manage return transaction) :include
(EZShop) .> (Manage sale transaction) :include
(EZShop) .> (Manage customers and cards) :include
(EZShop) .> (Manage products) :include
(EZShop) .> (Manage inventory and orders) :include
(Manage sale transaction) .> (Manage payment) :include
(Manage payment) <|- (Payment cash)
(Manage payment) <|- (Payment by credit card)
(Manage inventory and orders) .> (Issue order) :include
(Manage inventory and orders) .> (Record arrival) :include
(Manage inventory and orders) .> (Pay order) :include
(Manage return transaction) .> (Manage return) :include
(Manage return) <|- (Return cash)
(Manage return) <|- (Return by credit card)
```


### Use case 1, UC1 - Manage products

| Actors Involved        | Administrator, Shop Manager |
| ------------- |:-------------:|
|  Precondition |  |
|  Post condition |  |
|  Nominal Scenario |  ShopManager creates a new product type PT populating its fields  |
|  Variants     | PT exists already, ShopManager modifies its fields  |
| | PT is assigned to an occupied location, issue warning |
| | PT is assigned to an existing barcode , issue warning |

##### Scenario 1-1

| Scenario |  Create product type X |
| ------------- |:-------------:| 
|  Precondition     | User C exists and is logged in |
|  Post condition     | X  into the system and with an assigned location  |
| Step#        | Description  |
|  1    |  C inserts new product description |  
|  2    |  C inserts new bar code |
|  3    |  C inserts new price per unit |
|  4    |  C inserts new product notes |
|  5    |  C enters location of X |
|  6    |  C confirms the entered data |


##### Scenario 1-2

| Scenario |  Modify product type location |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Product type X exists |
|  | Location L is free |
|  Post condition     | X.location = L |
| Step#        | Description  |
|  1    |  C searches X via bar code |
|  2    |  C selects X's record |
|  3    |  C selects a new product location |

##### Scenario 1-3

| Scenario |  Modify product type price per unit |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Product type X exists |
|  Post condition     | X.pricePerUnit = new Price |
| Step#        | Description  |
|  1    |  C searches X via bar code |
|  2    |  C selects X's record |
|  3    |  C inserts a new price > 0 |
|  4   |  C confirms the update |
|  5   |  X is updated |

### Use case 2, UC2 - Manage users and rights

| Actors Involved        | Administrator |
| ------------- |:-------------:|
|  Precondition | Administrator A logged in |
|  Post condition |  |
|  Nominal Scenario |  A defines a new user and its access rights  |
|  Variants     | A modifies fields or access rights of an existing user |

##### Scenario 2-1

| Scenario |  Create user and define rights |
| ------------- |:-------------:| 
|  Precondition     | Admin A exists and is logged in |
|  Post condition     | Account X is created |
| Step#        | Description  |
|  1    |  A defines the credentials of the new Account X |  
|  2    |  A selects the access rights for the new account X |
|  3    |  C confirms the inserted data |


##### Scenario 2-2

| Scenario |  Delete user |
| ------------- |:-------------:| 
|  Precondition     | Admin A exists and is logged in |
|  | Account X exists |
|  Post condition     | Account X deleted |
| Step#        | Description  |
|  1    |  A selects account X  |
|  2    |  X deleted from the system |

##### Scenario 2-3

| Scenario |  Modify user rights |
| ------------- |:-------------:| 
|  Precondition     | Admin A exists and is logged in |
|  | Account X exists |
|  Post condition     | X's rights updated |
| Step#        | Description  |
|  1    |  A selects account X  |
|  2    |  A selects the access rights for X |
|  3    |  A confirms the inserted data |


### Use case 3, UC3 - Manage inventory and orders

| Actors Involved        | Administrator, Shop Manager |
| ------------- |:-------------:|
|  Precondition | Product type PT exists |
|  Post condition | Order O for PT exists  |
|  Nominal Scenario |  Manager creates new order O for product PT. Manager sends order to vendor and pays the agreed price. When order arrives to the shop, manager records order arrival. |
|  Variants     | Creation of order, PT does not exist, issue warning |
|  | Not enough money in balance to send order, issue warning |
|  | Product PT has no location assigned when registering an order arrival, issue warning |

##### Scenario 3-1

| Scenario |  Order of product type X issued |
| ------------- |:-------------:| 
|  Precondition     | ShopManager S exists and is logged in |
| | Product type X exists |
|  Post condition     | Order O exists and is in ISSUED state  |
| | Balance not changed |
| | X.units not changed |
| Step#        | Description  |
|  1    | S creates order O |
|  1    |  S fills  quantity of product to be ordered and the price per unit |  
|  3    |  O is recorded in the system in ISSUED state |

##### Scenario 3-2

| Scenario |  Order of product type X payed |
| ------------- |:-------------:| 
|  Precondition     | ShopManager S exists and is logged in |
| | Product type X exists |
| | Balance >= Order.units * Order.pricePerUnit |
| | Order O exists | 
|  Post condition     | Order O is in PAYED state  |
| | Balance -= Order.units * Order.pricePerUnit |
| | X.units not changed |
| Step#        | Description  |
|  1    |  S search for Order O |  
|  2    |  S register payment done for O |
|  3    |  O's state is updated to PAYED |

##### Scenario 3-3

| Scenario |  Record order of product type X arrival |
| ------------- |:-------------:| 
|  Precondition     | ShopManager S exists and is logged in |
| | Product type X exists |
| | X.location is valid |
| | Order O exists and is in PAYED state  |
|  Post condition     | O is in COMPLETED state  |
| | X.units += O.units |
| Step#        | Description  |
|  1    |  O arrives to the shop |  
|  2    |  S records O arrival in the system  |
|  3    |  The system updates X available quantity |
|  4    |  O is updated in the system in COMPLETED state |

### Use case 4, UC4 - Manage Customers and  Cards

| Actors Involved        | Administrator, Shop Manager, Cashier |
| ------------- |:-------------:|
|  Precondition     |                                                         |
|  Post condition     |                Account U for customer Cu exists                  |
|  Nominal Scenario     |  User creates (if not existing) a new account for Cu, creates a new loyalty card and attaches to Cu  |
|  Variants     |  User modifies customer data|
|  Variants     |  User deletes customer data|

##### Scenario 4-1

| Scenario |  Create customer record |
| ------------- |:-------------:| 
|  Precondition     | Account U for Customer Cu not existing  |
|  Post condition     | U is  into the system  |
| Step#        | Description  |
|  1    |  User asks Cu personal data |
|  2    |  USer fills U's fields with Cu's personal data |  
|  3    |  User confirms  |


##### Scenario 4-2

| Scenario |  Attach Loyalty card to customer record |
| ------------- |:-------------:| 
|  Precondition     | Account U for Customer Cu existing  |
|  Post condition     | Loyalty card L attached to U |
| Step#        | Description  |
|  1    |  User creates a new L with a unique serial number |
|  2    |  User attaches L to U  |  


##### Scenario 4-3

| Scenario |  Detach Loyalty card from customer record |
| ------------- |:-------------:| 
|  Precondition     | Account U for Customer Cu existing  |
| | Loyalty card L attached to U |
|  Post condition     | Loyalty card L detached from U |
| Step#        | Description  |
|  1    |  User selects customer record U |
|  2    |  User  detaches L from U  |  
|  3    |  U is updated |

##### Scenario 4-4

| Scenario |  Update customer record |
| ------------- |:-------------:| 
|  Precondition     | Account U for Customer Cu existing  |
|  Post condition     | U updated |
| Step#        | Description  |
|  1    |  User selects customer record U |
|  2    |  User modifies personal data of Cu  | 


### Use case 5, UC5 - Authenticate, authorize

| Actors Involved        | Administrator, Shop Manager, Cashier |
| ------------- |:-------------:|
|  Precondition     |                                                       |
|  Post condition     |                                 |
|  Nominal Scenario     | Login: user enters credentials, system checks credentials, user is authenticated   |
|  Variants     | Login, credentials wrong, user not authenticated |
|  | Logout |


##### Scenario 5-1

| Scenario |  Login |
| ------------- |:-------------:| 
|  Precondition     | Account  for User U existing  |
|  Post condition     | U logged in  |
| Step#        | Description  |
|  1    |  User inserts his username |
|  2    |  User inserts his password |
|  3    |  User logged in,  system shows the functionalities offered by the access priviledges of  U |


##### Scenario 5-2

| Scenario |  Logout |
| ------------- |:-------------:| 
|  Precondition     | U logged-in  |
|  Post condition     | U logged-out  |
| Step#        | Description  |
|  1    |  Employee logs out |
|  2    |  The system shows the login/sign in page |  


### Use case 6, UC6 - Manage sale transaction

| Actors Involved        |  Administrator, Shop Manager, Cashier |
| ------------- |:-------------:|
|  Precondition     | There are products available in the store |
|  | No sale transaction has already been opened with the instance of EzShop under consideration |
|  Post condition     | Sale is recorded, Ticket T is created |
|  Nominal Scenario   | Employee starts a new sale transaction and then, for each product P in the customer's cart, he records the quantity to be added to the transaction. The relative available quantity of P available in store is updated. The transaction is closed |
|  Variants     | discount rate to be applied to a product P  |
| | discount rate applied to the whole sale |


##### Scenario 6-1 

| Scenario |  Sale of product type X completed |
| ------------- |:-------------:| 
|  Precondition     | Cashier C exists and is logged in |
| | Product type X exists and has enough units to complete the sale |
|  Post condition     | Balance += N*X.unitPrice  |
| | X.quantity -= N |
| Step#        | Description  |
|  1    |  C starts a new sale transaction |  
|  2    |  C reads bar code of X |
|  3    |  C adds N units of X to the sale |
|  4    |  X available quantity is decreased by N |
|  5    |  C closes the sale transaction |
|  6    |  System asks payment type |
|  7    |  Manage  payment (see UC7) |
|  8    |  Payment successful |
|  9   |  C confirms the sale and prints the sale Ticket |
|  10   |  Balance is updated |

##### Scenario 6-2

| Scenario |  Sale of product type X with product discount |
| ------------- |:-------------:| 
|  Precondition     | Cashier C exists and is logged in |
| | Product type X exists and has enough units to complete the sale |
|  Post condition     | Balance += (N*X.unitPrice - N*X.unitPrice*ProductDiscount)  |
| | X.quantity -= N |
| Step#        | Description  |
|  1    |  C starts a new sale transaction |  
|  2    |  C reads bar code of X |
|  3    |  C adds N units of X to the sale |
|  4    |  X available quantity is decreased by N |
|  5    |  C applies a discount rate for X |
|  6    |  C closes the sale transaction |
|  7    |  Sytem ask payment type |
|  8    |  Manage  payment (go to UC 7) |
|  9   |  Payment successful |
|  10   |  C confirms the sale and prints the sale Ticket |
|  11   |  Balance is updated |

##### Scenario 6-3

| Scenario |  Sale of product type X with sale discount |
| ------------- |:-------------:| 
|  Precondition     | Cashier C exists and is logged in |
| | Product type X exists and has enough units to complete the sale |
|  Post condition     | Balance += (N*X.unitPrice) - N*X.unitPrice*SaleDiscount)  |
| | X.quantity -= N |
| Step#        | Description  |
|  1    |  C starts a new sale transaction |  
|  2    |  C reads bar code of X |
|  3    |  C adds N units of X to the sale |
|  4    |  X available quantity is decreased by N |
|  5    |  C applies a sale discount rate |
|  6    |  C closes the sale transaction |
|  7    |  Sytem ask payment type |
|  8    |  Manage  payment (go to UC 7 ) |
|  9    |  Payment successful |
|  10   |  C confirms the sale and prints the sale Ticket |
|  11   |  Balance is updated |

##### Scenario 6-4

| Scenario |  Sale of product type X with Loyalty Card update |
| ------------- |:-------------:| 
|  Precondition     | Cashier C exists and is logged in |
| | Product type X exists and has enough units to complete the sale |
| | Customer Cu has a Loyalty Card L |
|  Post condition     | Balance += N*X.unitPrice  |
| | X.quantity -= N |
| | L.points += (N*X.unitPrice)/10 |
| Step#        | Description  |
|  1    |  C starts a new sale transaction |  
|  2    |  C reads bar code of X |
|  3    |  C adds N units of X to the sale |
|  4    |  X available quantity is decreased by N |
|  5    |  C closes the sale transaction |
|  6    |  Sytem ask payment type |
|  7    |  C reads L serial number |
|  8    |  Manage credit card payment (go to scenario 3) |
|  9   |  Payment successful |
|  10   |  L.points updated |
|  11   |  C confirms the sale and prints the sale Ticket |
|  12   |  Balance is updated |

##### Scenario 6-5

| Scenario |  Sale of product type X cancelled |
| ------------- |:-------------:| 
|  Precondition     | Cashier C exists and is logged in |
| | Product type X exists and has enough units to complete the sale |
|  Post condition     | Balance not changed  |
| | X.quantity not changed |
| Step#        | Description  |
|  1    |  C starts a new sale transaction |
|  2    |  C reads bar code of X |
|  3    |  C adds N units of X to the sale |
|  4    |  X available quantity is decreased by N |
|  5    |  C closes the sale transaction |
|  6    |  System ask payment type |
|  7    |  Customer cancels the payment |
|  8    |  Sale transaction aborted |
|  9   |  Sale ticket deleted, no change will be recorded |

##### Scenario 6-6

| Scenario |  Sale of product type X completed (Cash) |
| ------------- |:-------------:| 
|  Precondition     | Cashier C exists and is logged in |
| | Product type X exists and has enough units to complete the sale |
|  Post condition     | Balance += N*X.unitPrice  |
| | X.quantity -= N |
| Step#        | Description  |
|  1    |  C starts a new sale transaction |  
|  2    |  C reads bar code of X |
|  3    |  C adds N units of X to the sale |
|  4    |  X available quantity is decreased by N |
|  5    |  C closes the sale transaction |
|  6    |  A sale review is shown to C and the Customer |
|  7    |  Sytem ask payment type |
|  8    |  Manage cash payment (UC7) |
|  9    |  Payment successful |
|  10   |  C confirms the sale and prints the sale Ticket |
|  11   |  Balance is updated |


### Use case 7, UC7 - Manage payment

| Actors Involved        | Administrator, Shop Manager, Cashier |
| ------------- |:-------------:|
|  Precondition     | amount to be received  is defined  |
|  Post condition     |   |
|  Nominal Scenario     |  read credit card number, check card, debit card |
| Variants      | Credit Card invalid (does not exist or stolen or expired), issue warning |
|   | Customer has not enough cash to pay the ticket, issue warning |
|   | Credit Card has not enough money, issue warning |


##### Scenario 7-1

| Scenario |  Manage payment by valid credit card |
| ------------- |:-------------:| 
|  Precondition     | Credit card C exists  |
|  Post condition     | C.Balance -= Price  |
| Step#        | Description  |
|  1    |  Read C.number |
|  2    |  Validate C.number with Luhn algorithm |  
|  3    |  Ask to credit sale price |
|  4    |  Price payed |
|  5    |  exit with success |

##### Scenario 7-2

| Scenario |  Manage payment by invalid credit card |
| ------------- |:-------------:| 
|  Precondition     | Credit card C does not exist  |
|  Post condition     |   |
| Step#        | Description  |
|  1    |  Read C.number |
|  2    |  Validate C.number with Luhn algorithm |  
|  3    |  C.number invalid, issue warning |
|  4    |  Exit with error |

##### Scenario 7-3

| Scenario |  Manage credit card payment with not enough credit |
| ------------- |:-------------:| 
|  Precondition     | Credit card C exists  |
| | C.Balance < Price |
|  Post condition     | C.Balance not changed  |
| Step#        | Description  |
|  1    |  Read C.number |
|  2    |  Validate C.number with Luhn algorithm |  
|  3    |  Ask to credit sale price |
|  4    |  Balance not sufficient, issue warning |
|  5    |  Exit with error |

##### Scenario 7-4

| Scenario |  Manage cash payment |
| ------------- |:-------------:| 
|  Precondition     | Cash >= Price  |
|  Post condition     |   |
| Step#        | Description  |
|  1    |  Collect banknotes and coins |
|  2    |  Compute cash quantity |  
|  3    |  Record cash payment |
|  4    |  Compute change |
|  5    |  Return change |

### Use case 8, UC8 - Manage return transaction

| Actors Involved        |  Administrator, Shop Manager, Cashier |
| ------------- |:-------------:|
|  Precondition     | Ticket T exists |
|  | No return transaction has already been opened with the instance of EzShop under consideration |
|  Post condition     | Balance updated |
|  | The available quantity of the products involved in the transaction is updated |
|  Nominal Scenario   | User  starts a new return transaction inserting the T's unique number. User selects product to be returned and quantity.  The customer receives a reimbursement equal to the value of the returned products, the balance is updated and the quantity of products available  is updated.  Reimbursement is cash if the Ticked was paid cash, by credit card if it was paid by credit card|
|  Variants     | Ticket number does not exists, issue warning |
|  |  Return payment failed, transaction rolled back |


##### Scenario 8-1

| Scenario |  Return transaction of product type X completed, credit card |
| ------------- |:-------------:| 
|  Precondition     | Cashier C exists and is logged in |
| | Product Type X exists |
| | Ticket T exists and has at least N units of X |
| | Ticket T was paid with credit card |
|  Post condition     | Balance -= N*T.priceForProductX  |
| | X.quantity += N |
| Step#        | Description  |
|  1    |  C inserts T.ticketNumber |
|  2    |  Return transaction starts |  
|  3    |  C reads bar code of X |
|  4    |  C adds N units of X to the return transaction |
|  5    |  X available quantity is increased by N |
|  6    |  Manage credit card return  (go to UC 10 ) |
|  7   |  Return successful, C closes the return transaction |
|  8   |  Balance is updated |

##### Scenario 8-2

| Scenario |  Return transaction of product type X completed, cash |
| ------------- |:-------------:| 
|  Precondition     | Cashier C exists and is logged in |
| | Product Type X exists |
| | Ticket T exists and has at least N units of X |
| | Ticket T was paid cash |
|  Post condition     | Balance -= N*T.priceForProductX  |
| | X.quantity += N |
| Step#        | Description  |
|  1    |  C inserts T.ticketNumber |
|  2    |  Return transaction starts |  
|  3    |  C reads bar code of X |
|  4    |  C adds N units of X to the return transaction |
|  5    |  X available quantity is increased by N |
|  6    |  Manage cash return (go to UC 10) |
|  7   |  Return  successful |
|  8   |  C confirms the return transaction and closes it  |
|  9   |  Sale Ticket is updated |
|  10   |  Balance is updated |




### Use case 9, UC9 - Accounting

| Actors Involved        | Administrator, Shop Manager |
| ------------- |:-------------:|
|  Precondition     |  |
|  Post condition     | List of all balance records returned |
|  Nominal Scenario     | The user enters two dates and the system returns the list of all the balance operations included between them. |

##### Scenario 9-1

| Scenario |  List credits and debits |
| ------------- |:-------------:| 
|  Precondition     | Manager C exists and is logged in |
|  Post condition     | Transactions list displayed  |
| Step#        | Description  |
|  1    |  C selects a start date |  
|  2    |  C selects an end date |
|  3    |  C sends transaction list request to the system |
|  4    |  The system returns the transactions list |
|  5    |  The list is displayed  |


### Use case 10, UC10 - Manage return

| Actors Involved        | Administrator, Shop Manager |
| ------------- |:-------------:|
|  Precondition     |  |
|  Post condition     | |
|  Nominal Scenario     | return by credit card |
|     Variant            | return cash |





##### Scenario 10-1

| Scenario |  Return payment by  credit card |
| ------------- |:-------------:| 
|  Precondition     | Credit card C exists  |
|  Post condition     |   |
| Step#        | Description  |
|  1    |  Read C.number |
|  2    |  Validate C.number with Luhn algorithm |  
|  3    |  Ask to return amount  |
|  4    |  amount returned  |
|  5    |  exit with success |




##### Scenario 10-2

| Scenario |  return  cash payment |
| ------------- |:-------------:| 
|  Precondition     |   |
|  Post condition     |   |
| Step#        | Description  |
|  1    |  Collect banknotes and coins |
|  2    |  Record cash return  |



# Glossary
```plantuml
left to right direction

class Shop
class AccountBook 
AccountBook - Shop
class FinancialTransaction {
 description
 amount
 date
}
AccountBook -- "*" FinancialTransaction

class Credit 
class Debit

Credit --|> FinancialTransaction
Debit --|> FinancialTransaction

class Order
class Sale
class Return

Order --|> Debit
Sale --|> Credit
Return --|> Debit


class ProductType{
    barCode
    description
    sellPrice
    quantity
    discountRate
    notes
}

Shop - "*" ProductType

class SaleTransaction {
    ID 
    date
    time
    cost
    paymentType
    discount rate
}
SaleTransaction - "*" ProductType

class Quantity {
    quantity
}
(SaleTransaction, ProductType)  .. Quantity

class LoyaltyCard {
    ID
    points
}

class Customer {
    name
    surname
}

LoyaltyCard "0..1" - Customer

SaleTransaction "*" -- "0..1" LoyaltyCard

class Product {
    
}

class Position {
    aisleID
    rackID
    levelID
}

ProductType - "0..1" Position

ProductType -- "*" Product : describes

class Order {
  supplier
  pricePerUnit
  quantity
  status
}

Order "*" - ProductType

class ReturnTransaction {
  quantity
  returnedValue
}

ReturnTransaction "*" - SaleTransaction
ReturnTransaction "*" - ProductType

note "ID is a number on 10 digits " as N1  
N1 .. LoyaltyCard
note "bar code is a number on 12 to 14  digits, compliant to GTIN specifications, see  https://www.gs1.org/services/how-calculate-check-digit-manually " as N2  
N2 .. ProductType
note "ID is a unique identifier of a transaction,  printed on the receipt (ticket number) " as N3
N3 .. SaleTransaction

```




# System Design


```plantuml
class EZShop
class BarCodeReader
class CreditCardReader
class Printer
class EZShopApplication
class LoyaltyCardReader
EZShop o-- BarCodeReader
EZShop o-- CreditCardReader
EZShop o-- Printer
EZShop o-- LoyaltyCardReader
EZShop o-- EZShopApplication
```

# Deployment Diagram
Stand alone application  model. 

```plantuml
artifact "EZShop Application" as ezshop
node "PC" as s

s -- ezshop
```

# Notes on development

EZShop is a system (including bar code reader, printer etc). However, for simplicity you are requested to 
develop a software only application. Further, the system interacts with other systems (credit card systems, suppliers).
These external systems are avoided or simulated. Further documentation is available here src/main/java/it/polito/ezshop/data/EZShopInterface.java

 * Hardware components
     * Printer: not available, print function not implemented
     * Bar code reader: not available, bar code provided as string of digits to API functions.
     * Credit card reader: not available, credit card number provided as string of digits to API functions
     * Loyalty card reader: not available, loyalty card number provided as string of digits to API functions


* Actors
    * Credit card circuit: interaction simulated using a file of credit cards 
   (src/main/java/it/polito/ezshop/utils/CreditCards.txt  )
    * Supplier: no email with orders sent to it (orders only generated and stored internally) 
