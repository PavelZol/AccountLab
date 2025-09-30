package me.pavelzol.bench;

import me.pavelzol.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@Warmup(iterations = 3, time = 1_000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1_000, timeUnit = TimeUnit.MILLISECONDS)
public class AccountBenchmarkTest {

    @State(Scope.Benchmark)
    public static class SharedState {
        SynchronizedBigDecimalAccount synchronizedBigDecimalAccount;
        NonBlockingBigDecimalAccount nonBlockingBigDecimalAccount;
        VarHandleBigDecimalAccount varHandleBigDecimalAccount;
        VarHandleBackoffAccount varHandleBackoffAccount;

        @Setup(Level.Trial)
        public void setup() throws NoSuchFieldException, IllegalAccessException {
            synchronizedBigDecimalAccount = new SynchronizedBigDecimalAccount(0.0);
            nonBlockingBigDecimalAccount = new NonBlockingBigDecimalAccount(0.0);
            varHandleBigDecimalAccount = new VarHandleBigDecimalAccount(0.0);
            varHandleBackoffAccount = new VarHandleBackoffAccount(0.0);
        }
    }

    @Benchmark
    public void synchronizedDebit(SharedState s) {
        s.synchronizedBigDecimalAccount.credit(1.0);
    }

    @Benchmark
    public void nonBlockingDebit(SharedState s) {
        s.nonBlockingBigDecimalAccount.credit(1.0);
    }

    @Benchmark
    public void varHandleDebit(SharedState s) {
        s.varHandleBigDecimalAccount.credit(1.0);
    }

    @Benchmark
    public void varHandleBackoffDebit(SharedState s) {
        s.varHandleBackoffAccount.credit(1.0);
    }

    static final void main(String... args) throws Exception {
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
