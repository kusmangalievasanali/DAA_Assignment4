
public class RecursiveEditDistance implements EditDistance {

    private long operations;

    public long getOperations() {
        return operations;
    }


    // State: solve(i, j) is the edit distance between the first i and j characters.
    // Transition: If characters match, solve(i-1, j-1); otherwise, 1 + min(delete, insert, replace).
    // Base case: If i == 0, return j; if j == 0, return i.

    @Override
    public int distance(String a, String b) {
        operations = 0;
        return solve(a, b, a.length(), b.length());
    }

    private int solve(String a, String b, int i, int j) {
        operations++;

        if (i == 0) {
            return j;
        }

        if (j == 0) {
            return i;
        }

        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return solve(a, b, i - 1, j - 1);
        }

        int delete = solve(a, b, i - 1, j);
        int insert = solve(a, b, i, j - 1);
        int replace = solve(a, b, i - 1, j - 1);

        return 1 + Math.min(delete, Math.min(insert, replace));
    }
}
