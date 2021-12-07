package it.polito.ezshop.data;

import java.util.ArrayList;
import java.util.List;

public class ReturnTransaction {
    private Integer id;
    private Integer saleTransactionId;
    private List<TicketEntry> entryList;
    private double price;
    private String state;
    private Integer balanceId;

    // list of RFID
    private List<String> RFIDList;

    public ReturnTransaction(Integer returnTransactionId, Integer saleTransactionId){
        this.id = returnTransactionId;
        this.saleTransactionId = saleTransactionId;
        this.price = 0.0;
        this.state = "OPEN";
        this.entryList = new ArrayList<>();
        this.RFIDList = new ArrayList<>();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSaleTransactionId() {
        return this.saleTransactionId;
    }

    public void setSaleTransactionId(Integer saleTransactionId) {
        this.saleTransactionId = saleTransactionId;
    }

    public List<TicketEntry> getEntryList() {
        return this.entryList;
    }

    public void setEntryList(List<TicketEntry> entryList) {
        this.entryList = entryList;
    }

    public List<String> getRFIDList() {
        return this.RFIDList;
    }

    public void setRFIDList(List<String> RFIDList) {
        this.RFIDList = RFIDList;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setState(String state){
        this.state = state;
    }

    public String getState(){
        return this.state;
    }

    public Integer getBalanceId() {return this.balanceId;}

    public void setBalanceId(Integer balanceId) {this.balanceId = balanceId;}
}
