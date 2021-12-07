package it.polito.ezshop.data;

public interface Order {

    Integer getBalanceId();

    void setBalanceId(Integer balanceId);

    String getProductCode();

    void setProductCode(String productCode);

    double getPricePerUnit();

    void setPricePerUnit(double pricePerUnit);

    int getQuantity();

    void setQuantity(int quantity);

    String getStatus();

    void setStatus(String status);

    Integer getOrderId();

    void setOrderId(Integer orderId);
}
