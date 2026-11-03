
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class StringSearchTest {

    private final NaiveSearch naive = new NaiveSearch();
    private final KMPSearch kmp = new KMPSearch();

    @Test
    void referenceLpsValues() {
        assertArrayEquals(
                new int[]{0, 1, 2, 0, 1, 2, 3, 3},
                kmp.lps("AAACAAAA")
        );

        assertArrayEquals(
                new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4},
                kmp.lps("ABABCABAB")
        );
    }

    @Test
    void referenceSearchValues() {
        checkBoth("ABABDABACDABABCABAB",
                "ABABCABAB", List.of(10));

        checkBoth("AAAAA", "AA",
                List.of(0, 1, 2, 3));

        checkBoth("ABABABA", "ABA",
                List.of(0, 2, 4));
    }

    @Test
    void overlappingMatches() {
        checkBoth("AAAAAA", "AAA",
                List.of(0, 1, 2, 3));

        checkBoth("ABABABAB", "ABAB",
                List.of(0, 2, 4));
    }

    @Test
    void noMatches() {
        checkBoth("ABCDEFG", "XYZ", List.of());
        checkBoth("", "ABC", List.of());
    }

    @Test
    void patternLongerThanText() {
        checkBoth("AB", "ABCD", List.of());
    }

    @Test
    void emptyPatternThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> naive.search("ABC", ""));

        assertThrows(IllegalArgumentException.class,
                () -> kmp.search("ABC", ""));
    }

    @Test
    void randomConsistency() {
        Random random = new Random(42);

        for (int t = 0; t < 100; t++) {
            String text = randomString(random, random.nextInt(51));
            String pattern = randomString(random, 1 + random.nextInt(10));

            assertEquals(
                    naive.search(text, pattern),
                    kmp.search(text, pattern)
            );
        }
    }

    private void checkBoth(String text, String pattern,
                           List<Integer> expected) {
        assertEquals(expected, naive.search(text, pattern));
        assertEquals(expected, kmp.search(text, pattern));
    }

    private String randomString(Random random, int length) {
        String alphabet = "ACGT";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(
                    random.nextInt(alphabet.length())
            ));
        }

        return sb.toString();
    }
}
