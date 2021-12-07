package it.polito.ezshop.data;

import java.time.LocalDate;

public interface BalanceOperation {

    int getBalanceId();

    void setBalanceId(int balanceId);

    LocalDate getDate();

    void setDate(LocalDate date);

    double getMoney();

    void setMoney(double money);

    String getType();

    void setType(String type);
}
