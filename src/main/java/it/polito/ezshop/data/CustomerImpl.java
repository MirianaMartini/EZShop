package it.polito.ezshop.data;

public class CustomerImpl implements Customer{

    private Integer id;
    private String name;
    private String card;
    private Integer points;

    @Override
    public String getCustomerName() {
        return this.name;
    }

    @Override
    public void setCustomerName(String customerName) {
        this.name = customerName;
    }

    @Override
    public String getCustomerCard() {
        return card;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        this.card = customerCard;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPoints() {
        return points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points = points;
    }

    public CustomerImpl(Integer id, String customerName){
        this.name = customerName;
        this.card = null;
        this.points = 0;
        this.id = id;
    }

    public CustomerImpl(Integer id, String customerName, String customerCard, Integer points){
        this.name = customerName;
        this.card = customerCard;
        this.points = points;
        this.id = id;
    }
}
