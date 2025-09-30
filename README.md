# Analysis of different implementations of contended Account class

## Given

Create an Account class that holds the current balance and provides this API:

- `void credit(double amount)`
- `void debit(double amount)`
- `double getBalance()`

The class is used in a multithreaded environment.

## Task

Select the most suitable implementation.

## Approaches

### Use of Integer instead of double

If values are limited to cents (two decimals), use an integer type and store amounts in cents; do all math with
integers.

This enables `int`/`long` with locks, or `AtomicInteger`/`AtomicLong` for non‑blocking algorithms.

However, assume we need finer fractions (e.g., BTC subunits or fractional shares).

### Use of `double`

:no_entry_sign: Not suitable. `double` introduces rounding errors.

### Use of `DoubleAdder`

:no_entry_sign: Not suitable. Thread‑safe, but still uses `double` and inherits rounding errors.

### Use of `BigDecimal`

:white_check_mark: The suitable option for precise math. `BigDecimal` avoids rounding errors, is immutable (allocates
per update), and is not thread‑safe; coordinate access.

#### `synchronized`

- Algorithm: Blocking
- API: :white_check_mark: Simplest; invariants are easy. Blocked threads are `blocked`, aiding deadlock diagnosis.
- No fair mode.
- Performance: Good uncontended throughput; lowest allocation rate.
- Example: [SynchronizedBigDecimalAccount.java](src/main/java/me/pavelzol/SynchronizedBigDecimalAccount.java)

#### `ReentrantLock`

- Algorithm: Blocking
- API: :white_check_mark: Clear; invariants are easy. Threads are `waiting`, which complicates deadlock analysis.
- Can be fair (but fair mode greatly hurts contended throughput).
- Performance: Very good contended; uncontended similar to `synchronized`; average allocations.
- Example: [ReentrantLockAccount.java](src/main/java/me/pavelzol/ReentrantLockAccount.java), [ReentrantFairLockAccount.java](src/main/java/me/pavelzol/ReentrantFairLockAccount.java)

#### `AtomicReference`

- Algorithm: Non‑blocking CAS
- API: :ballot_box_with_check: Simple but careful; invariants are harder. Prone to ABA; requires use of backoff with
  jitter under contention.
- No fair mode.
- Performance: Best uncontended; backoff needed for good contended performance.
- Examples: [AtomicReferenceAccount.java](src/main/java/me/pavelzol/AtomicReferenceAccount.java), [AtomicReferenceBackoffAccount.java](src/main/java/me/pavelzol/AtomicReferenceBackoffAccount.java)

#### `VarHandle`

- Algorithm: Non‑blocking CAS
- API: :bangbang: Easy to misuse (e.g., wrong acquire). Prone to ABA; requires use of backoff with jitter under
  contention.
- No fair mode.
- Performance: Worst uncontended; backoff improves contended performance.
- Examples: [VarHandleAccount.java](src/main/java/me/pavelzol/VarHandleAccount.java), [VarHandleBackoffAccount.java](src/main/java/me/pavelzol/VarHandleBackoffAccount.java)

## Benchmark Measurements

| Solution                  | Throughput Contended (ops/ms) | Allocation Contended (B/op) | Throughput Single Thread (ops/ms) | Allocation Single Thread (B/op) |
|---------------------------|-------------------------------|-----------------------------|-----------------------------------|---------------------------------|
| synchronized              | 8 499 ±433                    | 40                          | 77 385 ±371                       | 40                              |
| reentrantLock (not fair)  | 46 636 ±2720                  | 120                         | 75 787 ±649                       | 40                              |
| reentrantLock (fair)      | 555 ±9                        | 152                         | 72 342 ±161                       | 40                              |
| AtomicReference           | 17 401 ±90                    | 260                         | 85 573 ±205                       | 40                              |
| AtomicReference (backoff) | 77 153 ±6513                  | 40                          | 84 983 ±86                        | 40                              |
| VarHandle (default)       | 17 966 ±101                   | 269                         | 60 618 ±155                       | 40                              |
| VarHandle (backoff)       | 55 713 ±2796                  | 40                          | 60 697 ±354                       | 40                              |

## Tests

- Correctness: Verifies each implementation reaches the expected final balance under concurrent credit/debit operations. Source: [CorrectnessTest.java](src/test/java/me/pavelzol/CorrectnessTest.java)
- Benchmark: JMH throughput benchmarks for credit operations across implementations (with GC profiling). Source: [AccountBenchmarkTest.java](src/test/java/me/pavelzol/bench/AccountBenchmarkTest.java)

### Run Benchmarks

- In your IDE, run the `main` method of `AccountBenchmarkTest`.

## Code NFR (assumed)

- Low contention.
- High chance of frequent code changes (thus simple API is preferred)
- High chance of invariants (locks are preferred)

## Conclusion

Given the NFRs, prefer `synchronized`. If constant high contention is expected, use a `ReentrantLock`.
