package me.pavelzol;

/**
 * JUnit correctness suite for all Account implementations.
 * Exercises concurrent credit/debit operations and checks the final balance.
 * Note: this test is expected to FAIL for implementations with rounding or
 * concurrency issues (by design) to highlight incorrect algorithms.
 */

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.concurrent.*;
import java.util.stream.Stream;

public class CorrectnessTest {
    private static Stream<Arguments> provideAccountImplementations() throws NoSuchFieldException, IllegalAccessException {
        return Stream.of(
            Arguments.of(new UnSynchronizedAccount(0.1)),
            Arguments.of(new SynchronizedPrimitiveAccount(0.1)),
            Arguments.of(new SynchronizedBigDecimalAccount(0.1)),
            Arguments.of(new ReentrantLockAccount(0.1)),
            Arguments.of(new ReentrantFairLockAccount(0.1)),
            Arguments.of(new DoubleAdderAccount(0.1)),
            Arguments.of(new AtomicReferenceAccount(0.1)),
            Arguments.of(new AtomicReferenceBackoffAccount(0.1)),
            Arguments.of(new VarHandleAccount(0.1))
        );
    }

    @ParameterizedTest
    @MethodSource("provideAccountImplementations")
    void testAccountImplementation(Account account) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors())) {
            for (int i = 0; i < 100_000; i++) {
                executor.submit(() -> {
                    try {
                        countDownLatch.await();
                        account.credit(0.1D);
                        account.debit(0.01D);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            countDownLatch.countDown();
        }
        Assertions.assertEquals(9_000.1D, account.getBalance(), account.getClass() + " has incorrect implementation!");
    }
}
