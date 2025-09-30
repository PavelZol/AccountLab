package me.pavelzol;

import java.math.BigDecimal;

public class SynchronizedBigDecimalAccount implements Account {
    private final Object lock = new Object();
    private volatile BigDecimal balance;

    public SynchronizedBigDecimalAccount(double initBalance) {
        this.balance = BigDecimal.valueOf(initBalance);
    }

    @Override
    public double getBalance() {
        return balance.doubleValue();
    }

    @Override
    public void debit(double delta) {
        synchronized (lock) {
            balance = balance.subtract(BigDecimal.valueOf(delta));
        }
    }

    @Override
    public void credit(double delta) {
        synchronized (lock) {
            balance = balance.add(BigDecimal.valueOf(delta));
        }
    }
}
