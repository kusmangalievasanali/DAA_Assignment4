
# DAA Assignment 4 — Dynamic Programming and String Algorithms

## Project Overview

This project implements and experimentally compares algorithms for Edit Distance and String Search.

The main objective is to investigate algorithm correctness, time complexity, space complexity, and performance on different input types.

The project uses Java 17, Maven, and JUnit 5.

## Implemented Algorithms

### Edit Distance

Three implementations of Levenshtein Distance are provided:

1. Recursive Edit Distance without memoization
2. Bottom-up Dynamic Programming using a full table
3. Space-optimized Dynamic Programming using two rows

All three implementations return the minimum number of insertions, deletions, and substitutions needed to transform one string into another.

### String Search

Two string-matching algorithms are implemented:

1. Naive String Search
2. Knuth-Morris-Pratt (KMP)

Both implementations return all matching positions, including overlapping matches.

KMP also provides an implementation of the Longest Proper Prefix which is also Suffix (LPS) array.

## Technologies

- Java 17
- Maven
- JUnit 5
- IntelliJ IDEA
- Git and GitHub

## Project Structure

```text
DAA_Assignment4/
├── pom.xml
├── README.md
├── REPORT.md
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── EditDistance.java
│   │       ├── RecursiveEditDistance.java
│   │       ├── TableEditDistance.java
│   │       ├── TwoRowsEditDistance.java
│   │       ├── NaiveSearch.java
│   │       ├── KMPSearch.java
│   │       ├── InputGenerator.java
│   │       ├── ExperimentRunner.java
│   │       ├── PlotGenerator.java
│   │       └── RatioPlotGenerator.java
│   └── test/
│       └── java/
│           ├── EditDistanceTest.java
│           └── StringSearchTest.java
└── results/
    ├── results.csv
    └── plots/
        ├── edit_distance.png
        ├── search_time.png
        ├── search_comparisons.png
        └── search_ratio.png
```

## How to Build and Test

Open the project in IntelliJ IDEA and allow Maven to load the dependencies.

In the Maven tool window, run:

Lifecycle → compile

To execute all JUnit tests, run:

Lifecycle → test

The test suite checks reference values, edge cases, overlapping matches, and randomized consistency.

## How to Run Experiments

Run the main method in:

`src/main/java/ExperimentRunner.java`

The experiment runner generates random and adversarial inputs, executes the algorithms five times per configuration, and records the measurements in:

`results/results.csv`

A fixed random seed of 42 is used for reproducibility.

## How to Generate Plots

Run these classes in IntelliJ IDEA:

1. `PlotGenerator.java`
2. `RatioPlotGenerator.java`

The generators produce SVG graphs in `results/plots/`.

PNG versions of the graphs are also included for submission.

The four graphs compare:

1. Full-table DP and Two-Rows DP execution time
2. Naive Search and KMP execution time
3. Character comparison counts
4. Naive-to-KMP execution-time ratio

## Experimental Methodology

The experiment uses different input sizes and two input types for each algorithm family.

Execution time is measured using `System.nanoTime()`.

Each configuration is executed five times, and the median execution time is used for analysis.

Algorithm-specific counters record recursive calls, computed DP cells, or character comparisons.

The complete experiment design, measurements, analysis, and conclusions are available in `REPORT.md`.

## Results

The experiments produced 220 measurements.

The results demonstrate that recursive Edit Distance becomes expensive for larger inputs, while dynamic programming avoids repeated subproblem calculations.

The two-row implementation reduces memory requirements compared with full-table DP.

KMP performs fewer character comparisons than Naive Search on adversarial inputs.

## Git Workflow

Development was organized using the following branches:

- `main` — final project
- `feature/plots` — experiment visualization
- `feature/documentation` — project documentation

The final release is marked with the Git tag `v1.0`.

## AI Use Acknowledgement

ChatGPT was used to assist with algorithm implementation, test development, experimental design, visualization code, and documentation.

The project was built and tested locally using IntelliJ IDEA and Maven. Algorithm correctness was checked using JUnit 5 tests, and performance measurements were collected through local execution.
