package me.pavelzol;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class SynchronizedPrimitiveAccount implements Account {
    private final Object lock = new Object();
    @GuardedBy("lock")
    private volatile double balance;

    public SynchronizedPrimitiveAccount(double initBalance) {
        this.balance = initBalance;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void debit(double delta) {
        synchronized (lock) {
            balance -= delta;
        }
    }

    @Override
    public void credit(double delta) {
        synchronized (lock) {
            balance += delta;
        }
    }
}
