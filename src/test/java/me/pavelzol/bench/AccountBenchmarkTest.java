package me.pavelzol.bench;

import me.pavelzol.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 3)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
public class AccountBenchmarkTest {

    @State(Scope.Benchmark)
    public static class SharedState {
        SynchronizedBigDecimalAccount synchronizedBigDecimalAccount;
        ReentrantLockAccount reentrantLockAccount;
        ReentrantFairLockAccount reentrantFairLockAccount;
        NonBlockingBigDecimalAccount nonBlockingBigDecimalAccount;
        VarHandleBigDecimalAccount varHandleBigDecimalAccount;
        VarHandleBackoffAccount varHandleBackoffAccount;

        @Setup(Level.Iteration)
        public void setup() throws NoSuchFieldException, IllegalAccessException {
            synchronizedBigDecimalAccount = new SynchronizedBigDecimalAccount(0.0);
            reentrantLockAccount = new ReentrantLockAccount(0.0);
            reentrantFairLockAccount = new ReentrantFairLockAccount(0.0);
            nonBlockingBigDecimalAccount = new NonBlockingBigDecimalAccount(0.0);
            varHandleBigDecimalAccount = new VarHandleBigDecimalAccount(0.0);
            varHandleBackoffAccount = new VarHandleBackoffAccount(0.0);
        }
    }

    @Benchmark
    public void synchronizedCredit(SharedState s) {
        s.synchronizedBigDecimalAccount.credit(1.0);
    }

    @Benchmark
    public void reentrantCredit(SharedState s) {
        s.reentrantLockAccount.credit(1.0);
    }

    @Benchmark
    public void reentrantFairCredit(SharedState s) {
        s.reentrantFairLockAccount.credit(1.0);
    }

    @Benchmark
    public void nonBlockingCredit(SharedState s) {
        s.nonBlockingBigDecimalAccount.credit(1.0);
    }

    @Benchmark
    public void varHandleCredit(SharedState s) {
        s.varHandleBigDecimalAccount.credit(1.0);
    }

    @Benchmark
    public void varHandleBackoffCredit(SharedState s) {
        s.varHandleBackoffAccount.credit(1.0);
    }

    public static void main(String... args) throws Exception {
        run(".*" + AccountBenchmarkTest.class.getSimpleName() + ".*");
    }

    private static void run(String includeRegex) throws Exception {
        int threads = Runtime.getRuntime().availableProcessors();
        Options opt = new OptionsBuilder()
            .include(includeRegex)
            .threads(threads)
            .shouldFailOnError(true)
            .build();
        new Runner(opt).run();
    }
}
