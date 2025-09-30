# Analyzis of different implementations of contented Account class

## Given

To create an Acccount class, which holds current balance of the account, and provides api to perform given operations:

- `void credit(double amount)`
- `void debit(double amount)`
- `double currentBalance()`

Class is going to be used in multithread environment.

## Task

Define which implementation is better suited.

## Approaches

### Use of Integer instead of double

If double is intended to store only "cents" of a currency, up to 2 signs after the dot, then it is advised to use
integer data type and do all operations - store and calculation with "cents".

Such approach will allow to either use `int` type in blocking algorithm, or use AtomicInteger in non-blocking
algorithms.

However, let's assume that we are required to build most flexible solution, which should handle fractions well (for
example fractions of Bitcoin, fractions of stock ownership).

## Use of `double`

Not suitable. `double` can create rounding errors.
Example:

## Use of `DoubleAdder`

Not suitable. `DoubleAdder` is thread safe, but still has rounding errors.
Example:

## Use of `BigDecimal`

The only suitable option. BigDecimal has precise calculations without rounding errors. BigDecimal is immutable class,
thus each operation creates a new instance, causing heavy allocation. Not threadsafe, thus requires additional thread
communication.

### `synchronized`

- Algorithm: Blocking
- API: :white_check_mark: The simplest. Easy to maintain invariants. Blocked threads will be in `blocked` state thus
  making it simple to investigate deadlocks.
- No fair mode.
- Performance: good throughput contended and uncontended, lowest memory allocation rate.

### `ReentrantLock`

- Algorithm:  Blocking
- API: :white_check_mark: Good enough. Easy to maintain invariants. Blocked threads will be in `waiting` state thus
  making it harder to investigate deadlocks.
- Can have fair mode (although avg throughput under contention is extremely poor).
- Performance: Very good contended throughput, uncontended similar to `synchronized`, average memory allocation rate.

### `AtomicReference`

- Algorithm:  Non-Blocking CAS
- API: :white_check_mark: Simple enough, but can be tricky. Difficult to maintain invariants. Can have ABA problem. For
  high contention requires backoff with random jitter.
- No fair mode.
- Performance: Best uncontended throughput, but requires backoff for good contended performance.

### `varHandle`

- Algorithm:  Non-Blocking CAS
- API: :bangbang: Requires careful handling. Can have ABA problem. Can be misused easily with, for example, incorrect
  acquiring. For high contention requires adding backoff and random jitter.
- No fair mode.
- Performance: Worst uncontended throughput, requires backoff for good contended performance.
- Examples:

## Benchmark Measurments

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

# Results

Given listed above NFRs, solution with `synchronized` block is going to be preferable. If there is a need for fairness -
ReentrantLocks also a good choice. 