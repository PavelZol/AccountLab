package me.pavelzol;

public class SynchronizedAccount implements Account {
    private final Object lock = new Object();
    private volatile double balance;

    public SynchronizedAccount(double initBalance) {
        this.balance = initBalance;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void debit(double delta) {
        synchronized (lock) {
            balance += delta;
        }
    }

    @Override
    public void credit(double delta) {
        synchronized (lock) {
            balance -= delta;
        }
    }
}
