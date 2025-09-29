package me.pavelzol;

import java.lang.invoke.*;
import java.math.BigDecimal;

public class VarHandleBigDecimalAccount implements Account {
    private final BigDecimal balance;
    private final VarHandle balanceHandle;

    public VarHandleBigDecimalAccount(double initBalance) throws NoSuchFieldException, IllegalAccessException {
        this.balance = BigDecimal.valueOf(initBalance);
        balanceHandle = MethodHandles.lookup().findVarHandle(this.getClass(), "balance", BigDecimal.class);
    }

    @Override
    public double getBalance() {
        return ((BigDecimal) balanceHandle.getVolatile(this)).doubleValue();
    }

    @Override
    public void debit(double delta) {
        BigDecimal curr;
        BigDecimal next;
        do {
            curr = (BigDecimal) balanceHandle.getVolatile(this);
            next = curr.add(BigDecimal.valueOf(delta));
        } while (!balanceHandle.compareAndSet(this, curr, next));
    }

    @Override
    public void credit(double delta) {
        BigDecimal curr;
        BigDecimal next;
        do {
            curr = (BigDecimal) balanceHandle.getVolatile(this);
            next = curr.subtract(BigDecimal.valueOf(delta));
        } while (!balanceHandle.compareAndSet(this, curr, next));
    }
}
