package me.pavelzol;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.DoubleAdder;

@ThreadSafe
public class DoubleAdderAccount implements Account {
    private final DoubleAdder balance;

    public DoubleAdderAccount(double initBalance) {
        this.balance = new DoubleAdder();
        this.balance.add(initBalance);
    }

    @Override
    public double getBalance() {
        return balance.doubleValue();
    }

    @Override
    public void debit(double delta) {
        balance.add(-delta);
    }

    @Override
    public void credit(double delta) {
        balance.add(delta);
    }
}
