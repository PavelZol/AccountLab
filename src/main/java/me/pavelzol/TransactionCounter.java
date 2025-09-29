package me.pavelzol;

import java.util.concurrent.atomic.AtomicInteger;

public class TransactionCounter {
    private final AtomicInteger count;

    public TransactionCounter() {
        count = new AtomicInteger();
    }

    public void countTransaction() {
        count.incrementAndGet();
    }

    public int getCurrentCount() {
        return count.get();
    }
}
