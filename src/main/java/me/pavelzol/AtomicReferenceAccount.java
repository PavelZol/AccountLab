package me.pavelzol;

import java.math.BigDecimal;
import java.util.concurrent.atomic.*;

public class AtomicReferenceAccount implements Account {
    private final AtomicReference<BigDecimal> balance;

    public AtomicReferenceAccount(double initBalance) {
        this.balance = new AtomicReference<>(BigDecimal.valueOf(initBalance));
    }

    @Override
    public double getBalance() {
        return balance.get().doubleValue();
    }

    @Override
    public void debit(double delta) {
        BigDecimal curr;
        BigDecimal next;
        do {
            curr = balance.get();
            next = curr.subtract(BigDecimal.valueOf(delta));
        } while (!balance.compareAndSet(curr, next));
    }

    @Override
    public void credit(double delta) {
        BigDecimal curr;
        BigDecimal next;
        do {
            curr = balance.get();
            next = curr.add(BigDecimal.valueOf(delta));
        } while (!balance.compareAndSet(curr, next));
    }
}
