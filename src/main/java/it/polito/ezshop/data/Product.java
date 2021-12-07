package it.polito.ezshop.data;

public class Product {
    private String rfid;
    private String barcode;
    private int saleId;

    public Product(String rfid, String barcode) {
        this.rfid = rfid;
        this.barcode = barcode;
        this.saleId = -1;
    }

    public Product(String rfid, String barcode, Integer saleId) {
        this.rfid = rfid;
        this.barcode = barcode;
        this.saleId = saleId;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }
}


