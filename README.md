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

| Solution                  | Throughput Contended (ops/ms) | Allocation Contended (B/op) | Throughput Uncontended (ops/ms) | Allocation Uncontended (B/op) |
|---------------------------|-------------------------------|-----------------------------|---------------------------------|-------------------------------|
| synchronized              | 8 499.580 ± 433.094           | 40.000 ± 0.001              | 77 385.259 ± 371.601            | 40.000 ± 0.001                |
| reentrantLock (not fair)  | 46 636.776 ± 2720.256         | 120.132 ± 0.019             | 75 787.697 ± 649.724            | 40.000 ± 0.001                |
| reentrantLock (fair)      | 555.873 ± 9.003               | 152.010 ± 0.045             | 72 342.081 ± 161.125            | 40.000 ± 0.001                |
| AtomicReference           | 17 401.695 ± 90.515           | 260.235 ± 9.710             | 85 573.843 ± 205.570            | 40.000 ± 0.001                |
| AtomicReference (backoff) | 77 153.816 ± 6513.619         | 40.035 ± 0.002              | 84 983.436 ± 86.502             | 40.000 ± 0.001                |
| VarHandle (default)       | 17 966.915 ± 101.895          | 269.261 ± 9.027             | 60 618.013 ± 155.265            | 40.000 ± 0.001                |
| VarHandle (backoff)       | 55 713.366 ± 2796.987         | 40.165 ± 0.001              | 60 697.546 ± 354.518            | 40.000 ± 0.001                |

## Code NFR (assumed)

- Low contention.
- High chance of frequent code changes (thus simple API is preferred)
- High chance of invariants (locks are preferred)

## Results

Given the NFRs, prefer `synchronized`. If constant high contention is expected, use a fair `ReentrantLock`.
