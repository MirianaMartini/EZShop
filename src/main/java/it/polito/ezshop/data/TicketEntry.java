package it.polito.ezshop.data;

public interface TicketEntry {

    String getBarCode();

    void setBarCode(String barCode);

    String getProductDescription();

    void setProductDescription(String productDescription);

    int getAmount();

    void setAmount(int amount);

    double getPricePerUnit();

    void setPricePerUnit(double pricePerUnit);

    double getDiscountRate();

    void setDiscountRate(double discountRate);

}
