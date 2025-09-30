package me.pavelzol.bench;

import me.pavelzol.AtomicReferenceAccount;
import me.pavelzol.AtomicReferenceBackoffAccount;
import me.pavelzol.ReentrantFairLockAccount;
import me.pavelzol.ReentrantLockAccount;
import me.pavelzol.SynchronizedBigDecimalAccount;
import me.pavelzol.VarHandleBackoffAccount;
import me.pavelzol.VarHandleAccount;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 3)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(1)
public class AccountBenchmarkTest {

    @State(Scope.Benchmark)
    public static class SharedState {
        SynchronizedBigDecimalAccount synchronizedBigDecimalAccount;
        ReentrantLockAccount reentrantLockAccount;
        ReentrantFairLockAccount reentrantFairLockAccount;
        AtomicReferenceAccount atomicReferenceAccount;
        AtomicReferenceBackoffAccount atomicReferenceBackoffAccount;
        VarHandleAccount varHandleAccount;
        VarHandleBackoffAccount varHandleBackoffAccount;

        @Setup(Level.Iteration)
        public void setup() throws NoSuchFieldException, IllegalAccessException {
            synchronizedBigDecimalAccount = new SynchronizedBigDecimalAccount(0.0);
            reentrantLockAccount = new ReentrantLockAccount(0.0);
            reentrantFairLockAccount = new ReentrantFairLockAccount(0.0);
            atomicReferenceAccount = new AtomicReferenceAccount(0.0);
            atomicReferenceBackoffAccount = new AtomicReferenceBackoffAccount(0.0);
            varHandleAccount = new VarHandleAccount(0.0);
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
    public void atomicReferenceCredit(SharedState s) {
        s.atomicReferenceAccount.credit(1.0);
    }

    @Benchmark
    public void atomicReferenceBackoffCredit(SharedState s) {
        s.atomicReferenceBackoffAccount.credit(1.0);
    }

    @Benchmark
    public void varHandleCredit(SharedState s) {
        s.varHandleAccount.credit(1.0);
    }

    @Benchmark
    public void varHandleBackoffCredit(SharedState s) {
        s.varHandleBackoffAccount.credit(1.0);
    }

    static void main() throws Exception {
        run(".*" + AccountBenchmarkTest.class.getSimpleName() + ".*");
    }

    private static void run(String includeRegex) throws Exception {
        Options opt = new OptionsBuilder()
            .include(includeRegex)
            .addProfiler(GCProfiler.class)
            .shouldFailOnError(true)
            .build();
        new Runner(opt).run();
    }
}
