
public class TwoRowsEditDistance implements EditDistance {

    private long operations;

    public long getOperations() {
        return operations;
    }

    // State: previous[j] and current[j] store edit distances for the previous and current rows.
    // Transition: If characters match, use previous[j-1]; otherwise, 1 + min(delete, insert, replace).
    // Base case: previous[j] = j and current[0] = i.

    @Override
    public int distance(String a, String b) {
        operations = 0;

        if (a.length() < b.length()) {
            String temp = a;
            a = b;
            b = temp;
        }

        int n = a.length();
        int m = b.length();

        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];

        for (int j = 0; j <= m; j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= n; i++) {
            current[0] = i;

            for (int j = 1; j <= m; j++) {
                operations++;

                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    current[j] = previous[j - 1];
                } else {
                    current[j] = 1 + Math.min(
                            previous[j],
                            Math.min(current[j - 1], previous[j - 1])
                    );
                }
            }

            int[] temp = previous;
            previous = current;
            current = temp;
        }

        return previous[m];
    }
}
