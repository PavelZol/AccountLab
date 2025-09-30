package me.pavelzol;

import net.jcip.annotations.ThreadSafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigDecimal;

@ThreadSafe
public class VarHandleAccount implements Account {
    @SuppressWarnings("FieldMayBeFinal")
    private BigDecimal balance;
    private final VarHandle balanceHandle;

    public VarHandleAccount(double initBalance) throws NoSuchFieldException, IllegalAccessException {
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
            next = curr.subtract(BigDecimal.valueOf(delta));
        } while (!balanceHandle.compareAndSet(this, curr, next));
    }

    @Override
    public void credit(double delta) {
        BigDecimal curr;
        BigDecimal next;
        do {
            curr = (BigDecimal) balanceHandle.getVolatile(this);
            next = curr.add(BigDecimal.valueOf(delta));
        } while (!balanceHandle.compareAndSet(this, curr, next));
    }
}
