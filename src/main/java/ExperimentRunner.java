
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ExperimentRunner {

    private static final int RUNS = 5;
    private static final int[] RECURSIVE_SIZES = {4, 6, 8, 10};
    private static final int[] DP_SIZES = {100, 200, 400, 800, 1600};
    private static final int[] SEARCH_SIZES = {1000, 4000, 16000, 64000};
    private static final int PATTERN_LENGTH = 20;

    private static final List<String> rows = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        rows.add("algorithm,input,n,time_ms,operations,run");

        runEditDistance();
        runStringSearch();

        Path directory = Path.of("results");
        Files.createDirectories(directory);
        Files.write(directory.resolve("results.csv"), rows);

        System.out.println("Experiments completed.");
        System.out.println("Results saved to results/results.csv");
    }

    private static void runEditDistance() {
        RecursiveEditDistance recursive = new RecursiveEditDistance();
        TableEditDistance table = new TableEditDistance();
        TwoRowsEditDistance twoRows = new TwoRowsEditDistance();

        for (String type : new String[]{"random", "different"}) {
            for (int n : RECURSIVE_SIZES) {
                String[] input = editInput(type, n);

                measureEdit("Recursive", type, n, input, recursive);
            }

            for (int n : DP_SIZES) {
                String[] input = editInput(type, n);

                measureEdit("TableDP", type, n, input, table);
                measureEdit("TwoRowsDP", type, n, input, twoRows);
            }
        }
    }

    private static String[] editInput(String type, int n) {
        if (type.equals("random")) {
            return InputGenerator.editDistanceRandom(n);
        }

        return InputGenerator.editDistanceDifferent(n);
    }

    private static void measureEdit(
            String name,
            String type,
            int n,
            String[] input,
            EditDistance algorithm
    ) {
        for (int run = 1; run <= RUNS; run++) {
            long start = System.nanoTime();

            algorithm.distance(input[0], input[1]);

            long end = System.nanoTime();

            long operations;

            if (algorithm instanceof RecursiveEditDistance r) {
                operations = r.getOperations();
            } else if (algorithm instanceof TableEditDistance t) {
                operations = t.getOperations();
            } else {
                operations = ((TwoRowsEditDistance) algorithm).getOperations();
            }

            addRow(name, type, n, end - start, operations, run);
        }
    }

    private static void runStringSearch() {
        NaiveSearch naive = new NaiveSearch();
        KMPSearch kmp = new KMPSearch();

        for (String type : new String[]{"random", "adversarial"}) {
            for (int n : SEARCH_SIZES) {
                String[] input;

                if (type.equals("random")) {
                    input = InputGenerator.searchRandom(n, PATTERN_LENGTH);
                } else {
                    input = InputGenerator.searchAdversarial(n, PATTERN_LENGTH);
                }

                for (int run = 1; run <= RUNS; run++) {
                    long startNaive = System.nanoTime();

                    naive.search(input[0], input[1]);

                    long endNaive = System.nanoTime();

                    addRow(
                            "NaiveSearch",
                            type,
                            n,
                            endNaive - startNaive,
                            naive.getComparisons(),
                            run
                    );

                    long startKmp = System.nanoTime();

                    kmp.search(input[0], input[1]);

                    long endKmp = System.nanoTime();

                    addRow(
                            "KMP",
                            type,
                            n,
                            endKmp - startKmp,
                            kmp.getComparisons(),
                            run
                    );
                }
            }
        }
    }

    private static void addRow(
            String algorithm,
            String input,
            int n,
            long nanoseconds,
            long operations,
            int run
    ) {
        double milliseconds = nanoseconds / 1_000_000.0;

        rows.add(String.format(
                Locale.US,
                "%s,%s,%d,%.6f,%d,%d",
                algorithm,
                input,
                n,
                milliseconds,
                operations,
                run
        ));
    }
}
