package me.pavelzol;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.math.BigDecimal;

@ThreadSafe
public class SynchronizedBigDecimalAccount implements Account {
    private final Object lock = new Object();
    @GuardedBy("lock")
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
