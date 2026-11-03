
import java.util.ArrayList;
import java.util.List;

public class KMPSearch {

    private long comparisons;

    public long getComparisons() {
        return comparisons;
    }

    public int[] lps(String p) {
        int m = p.length();
        int[] lps = new int[m];

        int len = 0;
        int i = 1;

        while (i < m) {
            comparisons++;

            if (p.charAt(i) == p.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
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

        int[] prefix = lps(pattern);

        int i = 0;
        int j = 0;

        while (i < n) {
            comparisons++;

            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == m) {
                    matches.add(i - j);
                    j = prefix[j - 1];
                }
            } else {
                if (j != 0) {
                    j = prefix[j - 1];
                } else {
                    i++;
                }
            }
        }

        return matches;
    }
}
