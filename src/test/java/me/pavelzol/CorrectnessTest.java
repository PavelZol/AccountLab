package me.pavelzol;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;

public class CorrectnessTest {
    public static final int CPU_MULTIPLIER = 4;

    @Test
    void testSynchronizedSolution() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        UnSynchronizedAccount account = new UnSynchronizedAccount(0);
        try (var executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * CPU_MULTIPLIER)) {
            for (int i = 0; i < 10_000; i++) {
                for (int j = 0; j < 2; j++) {
                    int finalI = i;
                    int finalJ = j;
                    executor.submit(() -> {
                        try {
                            countDownLatch.await();
                            if (finalJ % 2 == 0) {
                                account.credit(finalI);
                            } else {
                                account.debit(finalI);
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
            countDownLatch.countDown();
        }
        Assertions.assertEquals(0D, account.getBalance());
    }
}
