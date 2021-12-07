package it.polito.ezshop.data;

import java.util.concurrent.atomic.AtomicInteger;

public class ProductTypeImpl implements ProductType{

    private int id;
    private String barcode;
    private String description;
    private double sellPrice;
    private int quantity;
    private String notes;
    private String location;

    public ProductTypeImpl(int id, String description, String barcode, double sellPrice, String notes ) {
        this.description=description;
        this.barcode=barcode;
        this.sellPrice=sellPrice;
        this.notes=notes;
        this.id=id;
    }

    @Override
    public String toString() {
        return "ProductTypeImpl{" +
                "id=" + id +
                ", barcode='" + barcode + '\'' +
                ", description='" + description + '\'' +
                ", sellPrice=" + sellPrice +
                ", quantity=" + quantity +
                ", notes='" + notes + '\'' +
                ", location='" + location + '\'' +
                '}';
    }


    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity=quantity;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location=location;
    }

    @Override
    public String getNote() {
        return notes;
    }

    @Override
    public void setNote(String note) {
        this.notes=note;
    }

    @Override
    public String getProductDescription() {
        return description;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.description=productDescription;
    }

    @Override
    public String getBarCode() {
        return barcode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barcode=barCode;
    }

    @Override
    public Double getPricePerUnit() {
        return sellPrice;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {
        this.sellPrice=pricePerUnit;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id=id;
    }

}

