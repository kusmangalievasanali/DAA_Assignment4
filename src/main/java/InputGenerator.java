
import java.util.Random;

public class InputGenerator {

    private static final long SEED = 42L;
    private static final String ALPHABET = "ACGT";

    public static String randomString(int length, long salt) {
        Random random = new Random(SEED + salt);
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(
                    random.nextInt(ALPHABET.length())
            ));
        }

        return sb.toString();
    }

    public static String[] editDistanceRandom(int n) {
        String a = randomString(n, n * 2L);
        String b = randomString(n, n * 2L + 1);
        return new String[]{a, b};
    }

    public static String[] editDistanceDifferent(int n) {
        return new String[]{
                "A".repeat(n),
                "B".repeat(n)
        };
    }

    public static String[] searchRandom(int n, int m) {
        String text = randomString(n, n * 3L);
        String pattern = randomString(m, n * 3L + 1);
        return new String[]{text, pattern};
    }

    public static String[] searchAdversarial(int n, int m) {
        String text = "A".repeat(n);
        String pattern = "A".repeat(m - 1) + "B";
        return new String[]{text, pattern};
    }
}
