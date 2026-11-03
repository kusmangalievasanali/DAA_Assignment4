
# Assignment 4 — Dynamic Programming and String Algorithms

## Experiment Design Card

### 1. Research Questions

1. How does the running time of recursive Edit Distance compare with dynamic programming?
2. How does input size affect the running time of the full-table and two-row Edit Distance implementations?
3. How do Naive Search and KMP perform on random and adversarial inputs?
4. How does the number of character comparisons change as the text length increases?

### 2. Algorithms

Edit Distance:
- Recursive Edit Distance without memoization
- Bottom-up Edit Distance using a full DP table
- Space-optimized Edit Distance using two rows

String Search:
- Naive String Search
- Knuth-Morris-Pratt (KMP)

### 3. Input Sizes

Recursive Edit Distance:
- n = 4, 6, 8, 10

Dynamic Programming Edit Distance:
- n = 100, 200, 400, 800, 1600

String Search:
- Text length n = 1000, 4000, 16000, 64000
- Pattern length m = 20

For Edit Distance, both strings have length n.

### 4. Input Types

Edit Distance:

1. Random: two strings generated from the alphabet ACGT.
2. Different: one string contains only A, and the other contains only B.

String Search:

1. Random: random text and pattern generated from ACGT.
2. Adversarial: text consists entirely of A, while the pattern contains 19 A characters followed by B.

The adversarial search input is designed to cause repeated partial matches in the naive algorithm.

### 5. Reproducibility

All random inputs are generated using a fixed seed of 42.

Each algorithm is executed five times for every input configuration.

The same input is used when comparing algorithms within the same family.

### 6. Measurements

For each execution, the following values are recorded:

- Algorithm name
- Input type
- Input size n
- Execution time in milliseconds
- Number of operations or character comparisons
- Run number

Time is measured using System.nanoTime().

The median of five executions is used for the plots.

Recursive Edit Distance counts recursive calls.

Both DP implementations count computed table cells.

Naive Search and KMP count character comparisons.

### 7. Predictions

Recursive Edit Distance is expected to become significantly slower as input size increases because it repeatedly solves overlapping subproblems.

Both dynamic programming implementations are expected to show approximately quadratic time growth, O(nm).

The two-row implementation should use less memory than the full-table implementation, although their running times may be similar.

Naive Search is expected to perform poorly on adversarial inputs because it repeatedly compares almost the entire pattern.

KMP is expected to perform fewer comparisons on adversarial inputs by using its LPS array.

### 8. Planned Plots

Plot 1: Input size versus median execution time for Table DP and Two-Rows DP.

Plot 2: Text length versus median execution time for Naive Search and KMP on adversarial inputs.

Plot 3: Text length versus character comparisons for Naive Search and KMP on adversarial inputs.

Plot 4: Ratio of Naive Search time to KMP time as text length increases.

Each plot will include axis labels, a legend, and a short interpretation.

### 9. Expected Complexity

| Algorithm | Time Complexity | Space Complexity |
|---|---|---|
| Recursive Edit Distance | Exponential worst case | O(n + m) |
| Table DP | O(nm) | O(nm) |
| Two-Rows DP | O(nm) | O(min(n,m)) |
| Naive Search | O(nm) | O(1), excluding output |
| KMP | O(n + m) | O(m), excluding output |

## Experimental Results

Results will be added after running the experiments.

## Conclusions

Conclusions will be added after analyzing the measurements.
