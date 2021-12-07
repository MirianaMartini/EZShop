package it.polito.ezshop.data;

public class TicketEntryImpl implements TicketEntry{
    private String barcode;
    private String productDesc;
    private int quantity;
    private double pricepUnit;
    private double discount;

    public TicketEntryImpl(String barCode, String productDescription,
                           int amount, double pricePerUnit, double discountRate){
        this.barcode = barCode;
        this.productDesc = productDescription;
        this.quantity = amount;
        this.pricepUnit = pricePerUnit;
        this.discount = discountRate;
    }

    @Override
    public String getBarCode() {
        return this.barcode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barcode = barCode;
    }

    @Override
    public String getProductDescription() {
        return this.productDesc;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.productDesc = productDescription;
    }

    @Override
    public int getAmount() {
        return this.quantity;
    }

    @Override
    public void setAmount(int amount) {
        this.quantity = amount;
    }

    @Override
    public double getPricePerUnit() {
        return this.pricepUnit;
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.pricepUnit = pricePerUnit;
    }

    @Override
    public double getDiscountRate() {
        return this.discount;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discount = discountRate;
    }
}
