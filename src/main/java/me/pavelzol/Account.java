package me.pavelzol;

public interface Account {
    double getBalance();
    void debit(double delta);
    void credit(double delta);
}
