# Change request  - Add RFID on each product


Each product has an RFID. RFID is a positive integer (received as a 10 characters string). The RFID is an identifier, as such is
unique (no two products can have the same RFID). The RFID is defined outside EzShop (EzShop application receives the RFID and is not supposed to produce them).

```plantuml
class ProductType{
    barCode
    description
    sellPrice
    quantity
    discountRate
    notes
}

class Product{
     RFID
}
ProductType -- "*" Product : describes
```

## Sale of a product
When a product is sold an RFID reader reads the RFID, no bar code reader is used. From the RFID 
the application retrieves the product type of the product, and all related information (like the price).
The sale transaction records each product sold. 
    see addProductToSaleRFID()   on API

## Reception of an order
When an order is received, each product received must be recorded, along with its RFID. 
    see recordOrderArrivalRFID()   on API

## Return of a product
When a product is returned, its RFID is read, the product is re inserted in the inventory. 
    see functions deleteProductFromSaleRFID(), returnProductRFID()   on API
