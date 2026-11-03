
import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class EditDistanceTest {

    private final EditDistance[] algorithms = {
            new RecursiveEditDistance(),
            new TableEditDistance(),
            new TwoRowsEditDistance()
    };

    @Test
    void referenceValues() {
        for (EditDistance algorithm : algorithms) {
            assertEquals(3, algorithm.distance("kitten", "sitting"));
            assertEquals(2, algorithm.distance("flaw", "lawn"));
            assertEquals(5, algorithm.distance("intention", "execution"));
            assertEquals(3, algorithm.distance("", "abc"));
            assertEquals(0, algorithm.distance("abc", "abc"));
        }
    }

    @Test
    void emptyStrings() {
        for (EditDistance algorithm : algorithms) {
            assertEquals(0, algorithm.distance("", ""));
            assertEquals(3, algorithm.distance("abc", ""));
            assertEquals(3, algorithm.distance("", "abc"));
        }
    }

    @Test
    void randomConsistency() {
        Random random = new Random(42);

        for (int t = 0; t < 100; t++) {
            String a = randomString(random, random.nextInt(7));
            String b = randomString(random, random.nextInt(7));

            int expected = algorithms[0].distance(a, b);

            assertEquals(expected, algorithms[1].distance(a, b));
            assertEquals(expected, algorithms[2].distance(a, b));
        }
    }

    private String randomString(Random random, int length) {
        String alphabet = "ACGT";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return sb.toString();
    }
}
