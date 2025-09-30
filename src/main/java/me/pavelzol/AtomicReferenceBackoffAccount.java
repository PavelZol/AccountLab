package me.pavelzol;

import net.jcip.annotations.ThreadSafe;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

@ThreadSafe
public class AtomicReferenceBackoffAccount implements Account {
    private final AtomicReference<BigDecimal> balance;

    public AtomicReferenceBackoffAccount(double initBalance) {
        this.balance = new AtomicReference<>(BigDecimal.valueOf(initBalance));
    }

    @Override
    public double getBalance() {
        return balance.get().doubleValue();
    }

    @Override
    public void debit(double delta) {
        int count = 0;
        BigDecimal curr;
        BigDecimal next;
        while (true) {
            curr = balance.get();
            next = curr.subtract(BigDecimal.valueOf(delta));
            if (balance.compareAndSet(curr, next)) {
                return;
            } else {
                count++;
                if (count > 10) {
                    long nanos = ThreadLocalRandom.current().nextLong(1_000, 50_000);
                    LockSupport.parkNanos(nanos);
                }
            }
        }
    }

    @Override
    public void credit(double delta) {
        int count = 0;
        BigDecimal curr;
        BigDecimal next;
        while (true) {
            curr = balance.get();
            next = curr.add(BigDecimal.valueOf(delta));
            if (balance.compareAndSet(curr, next)) {
                return;
            } else {
                count++;
                if (count > 10) {
                    long nanos = ThreadLocalRandom.current().nextLong(1_000, 50_000);
                    LockSupport.parkNanos(nanos);
                }
            }
        }
    }
}
