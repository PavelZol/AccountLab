package me.pavelzol;

import java.lang.invoke.*;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.LockSupport;

public class VarHandleBackoffAccount implements Account {
    private BigDecimal balance;
    private final VarHandle balanceHandle;

    public VarHandleBackoffAccount(double initBalance) throws NoSuchFieldException, IllegalAccessException {
        this.balance = BigDecimal.valueOf(initBalance);
        balanceHandle = MethodHandles.lookup().findVarHandle(this.getClass(), "balance", BigDecimal.class);
    }

    @Override
    public double getBalance() {
        return ((BigDecimal) balanceHandle.getVolatile(this)).doubleValue();
    }

    @Override
    public void debit(double delta) {
        int count = 0;
        BigDecimal curr;
        BigDecimal next;
        while (true) {
            curr = (BigDecimal) balanceHandle.getVolatile(this);
            next = curr.subtract(BigDecimal.valueOf(delta));
            if (balanceHandle.compareAndSet(this, curr, next)) {
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
            curr = (BigDecimal) balanceHandle.getVolatile(this);
            next = curr.add(BigDecimal.valueOf(delta));
            if (balanceHandle.compareAndSet(this, curr, next)) {
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
