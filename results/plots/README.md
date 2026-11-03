
# Assignment 4 — Dynamic Programming and String Algorithms

## Project Description

This project implements and compares five algorithms for edit distance and string matching.

The project was developed in Java using Maven and JUnit 5.

## Algorithms

### Edit Distance

1. Recursive Edit Distance without memoization
2. Bottom-up Dynamic Programming using a full table
3. Space-optimized Dynamic Programming using two rows

All three implementations calculate the Levenshtein distance.

### String Search

1. Naive String Search
2. Knuth-Morris-Pratt (KMP)

Both algorithms return all matching positions, including overlapping matches.

## Requirements

- Java 17 or newer
- Maven
- JUnit 5

## Build and Test

Open the project in IntelliJ IDEA.

Use the Maven tool window:

Lifecycle → compile

To run the tests:

Lifecycle → test

The tests verify reference values, edge cases, and randomized consistency.

## Running Experiments

Run the main method in:

src/main/java/ExperimentRunner.java

The program executes five runs per input configuration and saves the results to:

results/results.csv

All random inputs use a fixed seed of 42.

## Generating Plots

Run:

src/main/java/PlotGenerator.java

Then run:

src/main/java/RatioPlotGenerator.java

The generated plots are stored in:

results/plots/

The PNG versions are included for the assignment submission.

## Experimental Results

The experiment compares execution time and algorithm-specific operation counters.

Each configuration is executed five times, and the median execution time is used for analysis.

Detailed results, predictions, plots, and conclusions are available in REPORT.md.

## Project Structure

- src/main/java/ — Algorithm implementations and experiment utilities
- src/test/java/ — JUnit 5 tests
- results/results.csv — Raw experimental measurements
- results/plots/ — Performance graphs
- REPORT.md — Experiment design, analysis, and conclusions
- README.md — Project documentation

## AI Use Acknowledgement

ChatGPT was used to assist with Java implementation, project structure, test development, experiment planning, and documentation.

The generated code and explanations were reviewed during project development. Algorithm correctness was checked using JUnit 5 tests, and experimental results were collected by running the implementations locally.
