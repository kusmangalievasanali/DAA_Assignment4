
import java.util.ArrayList;
import java.util.List;

public class NaiveSearch {

    private long comparisons;

    public long getComparisons() {
        return comparisons;
    }

    public List<Integer> search(String text, String pattern) {
        comparisons = 0;

        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be empty");
        }

        List<Integer> matches = new ArrayList<>();

        int n = text.length();
        int m = pattern.length();

        if (m > n) {
            return matches;
        }

        for (int i = 0; i <= n - m; i++) {
            int j = 0;

            while (j < m) {
                comparisons++;

                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }

                j++;
            }

            if (j == m) {
                matches.add(i);
            }
        }

        return matches;
    }
}
