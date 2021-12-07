package it.polito.ezshop.data;

import java.util.ArrayList;
import java.util.List;

public class SaleTransactionImpl implements SaleTransaction{
    private Integer id;
    private List<TicketEntry> entryList;
    private double discount;
    private double totalPrice;
    private String stState;
    private Integer balanceId;

    // list of RFID
    private List<String> RFIDList;

    public SaleTransactionImpl(int ticketNumber){
        this.id = ticketNumber;
        this.discount = 0.0;
        this.totalPrice = 0.0;
        this.stState = "OPEN";
        this.entryList = new ArrayList<>();
        this.RFIDList = new ArrayList<>();
        //Set an impossible number for the balanceId to let new SaleTransaction to be added in the DB
        this.balanceId = null;
    }

    @Override
    public Integer getTicketNumber() {
        return this.id;
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        this.id = ticketNumber;
    }

    @Override
    public List<TicketEntry> getEntries() {
        return this.entryList;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.entryList = entries;
    }

    public List<String> getRFIDList() {
        return this.RFIDList;
    }

    public void setRFIDList(List<String> RFIDList) {
        this.RFIDList = RFIDList;
    }

    @Override
    public double getDiscountRate() {
        return this.discount;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discount = discountRate;
    }

    @Override
    public double getPrice() {
        return this.totalPrice;
    }

    @Override
    public void setPrice(double price) {
        this.totalPrice = price;
    }

    public void setState(String state){
        this.stState = state;
    }

    public String getState() { return this.stState; }

    public Integer getBalanceId() {return this.balanceId;}

    public void setBalanceId(Integer balanceId) {this.balanceId = balanceId;}

}
