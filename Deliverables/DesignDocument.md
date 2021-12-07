# Design Document 


Authors:  Biasi Cristina 281936, Cielo Fabio 292464, Guarnieri Enea 292561, Martini Miriana 283238

Date: 30/04/2021

Version: 1.0

Version: 1.1 - 26/05/2021<br/>
Low level design and Verification Sequence diagrams modified after coding and testing

Version: 1.2 - 04/05/2021<br/>
High level design and Verification Traceability matrix modified after coding and testing

Version 2.0 - 12/06/2021
Changes in document due to the implementation of Change Request - Add RFID on each product


# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 

The Architecture Pattern choosen is MVC (Model View Controller). 
The EZShopGUI package implements the View, while EZShopData package is related to Model and Data. 
An additional EZShopException package manages exceptions that can be throwned by EZShop.

### Package diagram

![Gantt Chart](images/packageDiagram.jpg)

# Low level design

The Design Pattern adopted is the Façade.
EZShop class implements the EZShop interface, calls methods of all other classes and contains all data structures needed to manage the application.

### Class diagram

![Class Diagram](images/ClassDiagram.jpg)

# Verification traceability matrix


|  | EZShop  | EZShopDB | User | ProductType | Order | BalanceOperation | Customer | LoyaltyCard | SaleTransaction | TicketEntry | ReturnTransaction |
| ------------- | :-------------:| :-------------:| :-------------:| :-------------:| :-------------:| :-------------:| :-------------:| :-------------:| :-------------:| :-------------: | :-------------: |
| FR1  | X | X | X |   |   |   |   |   |   |   |   |
| FR3  | X | X | X | X |   |   |   |   |   |   |   |
| FR4  | X | X | X | X | X | X |  |   |   |   |   |
| FR5  | X | X | X |   |   |   | X | X |   |   |   |
| FR6  | X | X | X | X |   | X | X | X | X | X | X |
| FR7  | X | X | X |   |   | X |   |   | X |   | X |
| FR8  | X | X | X |   | X | X |   |   | X |   | X |









# Verification sequence diagrams 


### SD1.1 - Scenario 1.1 Create product type X
![Sequence Diagrama 1.1](images/SequenceDiagram1.1.png)

### SD1.2 - Scenario 1.2 Modify product type location
![Sequence Diagrama 1.2](images/SequenceDiagram1.2.png)

### SD1.3 - Scenario 1.3 Modify product type price per unit
![Sequence Diagrama 1.3](images/SequenceDiagram1.3.png)

### SD2.1 - Scenario 2.1 Create user and define rights
![Sequence Diagrama 2.1](images/SequenceDiagram2.1.png)

### SD2.2 - Scenario 2.2 Delete user
![Sequence Diagrama 2.2](images/SequenceDiagram2.2.png)

### SD2.3 - Scenario 2.3 Modify user rights
![Sequence Diagrama 2.3](images/SequenceDiagram2.3.png)

### SD3.1 - Scenario 3.1 Order of product type X issued
![Sequence Diagrama 3.1](images/SequenceDiagram3.1.png)

### SD3.2 - Scenario 3.2 Order of product type X payed
![Sequence Diagrama 3.2](images/SequenceDiagram3.2.png)

### SD3.3 - Scenario 3.3 Record order of product type X arrival
![Sequence Diagrama 3.3](images/SequenceDiagram3.3.png)

### SD4.1 - Scenario 4.1 Create customer record
![Sequence Diagrama 4.1](images/SequenceDiagram4.1.png)

### SD4.2 - Scenario 4.2 Attach Loyalty card to customer record
![Sequence Diagrama 4.2](images/SequenceDiagram4.2.png)

### SD4.3 - Scenario 4.3 Detach Loyalty card from customer record
![Sequence Diagrama 4.3](images/SequenceDiagram4.3.png)

### SD4.4 - Scenario 4.4 Update customer record
![Sequence Diagrama 4.4](images/SequenceDiagram4.4.png)

### SD5.1 - Scenario 5.1 Login
![Sequence Diagrama 5.1](images/SequenceDiagram5.1.png)

### SD5.2 - Scenario 5.2 Logout
![Sequence Diagrama 5.2](images/SequenceDiagram5.2.png)

### SD6.1 - Scenario 6.1 Sale of product type X completed
![Sequence Diagrama 6.1](images/SequenceDiagram6.1.png)

### SD6.2 - Scenario 6.2 Sale of product type X with product discount
![Sequence Diagrama 6.2](images/SequenceDiagram6.2.png)

### SD6.3 - Scenario 6.3 Sale of product type X with product discount
![Sequence Diagrama 6.3](images/SequenceDiagram6.3.png)

### SD6.5 - Scenario 6.5 Sale of product type X cancelled
![Sequence Diagrama 6.5](images/SequenceDiagram6.5.png)

### SD7.1 - Scenario 7.1 Manage payment by valid credit card
![Sequence Diagrama 7.1](images/SequenceDiagram7.1.jpg)

### SD7.2 - Scenario 7.2 Manage payment by invalid credit card
![Sequence Diagrama 7.2](images/SequenceDiagram7.2.jpg)

### SD7.3 - Scenario 7.3 Manage credit card payment with not enough credit
![Sequence Diagrama 7.3](images/SequenceDiagram7.3.jpg)

### SD7.4 - Scenario 7.4 Manage cash payment 
![Sequence Diagrama 7.4](images/SequenceDiagram7.4.jpg)

### SD8.1 - Scenario 8.1 Return transaction of product type X completed, credit card
![Sequence Diagrama 8.1](images/SequenceDiagram8.png)

### SD9.1 - Scenario 9.1 List credits and debits
![Sequence Diagrama 9.1](images/SequenceDiagram9.1.png)

### SD10.1 - Scenario 10.1 Return payment by credit card
![Sequence Diagrama 10.1](images/SequenceDiagram10.1.png)

### SD10.2 - Scenario 10.2 Return cash payment 
![Sequence Diagrama 10.2](images/SequenceDiagram10.2.png)
