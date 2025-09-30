package me.pavelzol;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantFairLockAccount implements Account {
    private final ReentrantLock lock = new ReentrantLock(true);
    private volatile BigDecimal balance;

    public ReentrantFairLockAccount(double initBalance) {
        this.balance = BigDecimal.valueOf(initBalance);
    }

    @Override
    public double getBalance() {
        return balance.doubleValue();
    }

    @Override
    public void debit(double delta) {
        lock.lock();
        try {
            balance = balance.subtract(BigDecimal.valueOf(delta));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void credit(double delta) {
        lock.lock();
        try {
            balance = balance.add(BigDecimal.valueOf(delta));
        } finally {
            lock.unlock();
        }
    }
}
