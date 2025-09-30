package me.pavelzol;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class UnSynchronizedAccount implements Account {
    private double balance;

    public UnSynchronizedAccount(double initBalance) {
        this.balance = initBalance;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void debit(double delta) {
        balance -= delta;
    }

    @Override
    public void credit(double delta) {
        balance += delta;
    }
}
