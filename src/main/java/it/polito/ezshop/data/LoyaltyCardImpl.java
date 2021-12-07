package it.polito.ezshop.data;

public class LoyaltyCardImpl {
    private String code;
    private boolean assigned;

    public LoyaltyCardImpl(String code, boolean assigned){
        this.code = code;
        this.assigned = assigned;
    }

    public String getCode() {
        return code;
    }

    public boolean isAssigned() {
        return assigned;
    }
}
