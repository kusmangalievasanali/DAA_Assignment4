
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class RatioPlotGenerator {

    public static void main(String[] args) throws IOException {
        Map<Integer, List<Double>> naive = new TreeMap<>();
        Map<Integer, List<Double>> kmp = new TreeMap<>();

        List<String> lines = Files.readAllLines(
                Path.of("results", "results.csv")
        );

        for (int i = 1; i < lines.size(); i++) {
            String[] p = lines.get(i).split(",");

            if (!p[1].equals("adversarial")) {
                continue;
            }

            int n = Integer.parseInt(p[2]);
            double time = Double.parseDouble(p[3]);

            if (p[0].equals("NaiveSearch")) {
                naive.computeIfAbsent(n, k -> new ArrayList<>()).add(time);
            } else if (p[0].equals("KMP")) {
                kmp.computeIfAbsent(n, k -> new ArrayList<>()).add(time);
            }
        }

        Map<Integer, Double> ratios = new TreeMap<>();

        for (int n : naive.keySet()) {
            if (!kmp.containsKey(n)) {
                continue;
            }

            double naiveMedian = median(naive.get(n));
            double kmpMedian = median(kmp.get(n));

            if (kmpMedian > 0) {
                ratios.put(n, naiveMedian / kmpMedian);
            }
        }

        if (ratios.isEmpty()) {
            throw new IllegalStateException("No ratio data found");
        }

        int width = 900;
        int height = 550;
        int left = 100;
        int right = 50;
        int top = 70;
        int bottom = 90;

        double maxX = ratios.keySet().stream()
                .mapToDouble(Integer::doubleValue).max().orElse(1);

        double maxY = Math.max(1, ratios.values().stream()
                .mapToDouble(Double::doubleValue).max().orElse(1) * 1.1);

        StringBuilder svg = new StringBuilder();

        svg.append(String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\">",
                width, height
        ));

        svg.append("<rect width=\"100%\" height=\"100%\" fill=\"white\"/>");

        svg.append("<text x=\"450\" y=\"35\" text-anchor=\"middle\" font-size=\"23\" font-family=\"Arial\">Naive / KMP Median Time Ratio</text>");

        svg.append(String.format(
                "<path d=\"M%d %d V%d H%d\" fill=\"none\" stroke=\"black\" stroke-width=\"2\"/>",
                left, top, height - bottom, width - right
        ));

        for (int tick = 0; tick <= 5; tick++) {
            double value = maxY * tick / 5.0;
            double y = height - bottom
                    - (height - top - bottom) * tick / 5.0;

            svg.append(String.format(
                    Locale.US,
                    "<text x=\"%d\" y=\"%.1f\" text-anchor=\"end\" font-size=\"13\" font-family=\"Arial\">%.2f</text>",
                    left - 12, y + 5, value
            ));
        }

        StringBuilder path = new StringBuilder();
        int index = 0;

        for (Map.Entry<Integer, Double> entry : ratios.entrySet()) {
            double x = left + entry.getKey() / maxX
                    * (width - left - right);

            double y = height - bottom - entry.getValue() / maxY
                    * (height - top - bottom);

            path.append(String.format(
                    Locale.US,
                    "%s %.2f %.2f ",
                    index++ == 0 ? "M" : "L", x, y
            ));

            svg.append(String.format(
                    Locale.US,
                    "<circle cx=\"%.2f\" cy=\"%.2f\" r=\"5\" fill=\"#2563eb\"/>",
                    x, y
            ));

            svg.append(String.format(
                    Locale.US,
                    "<text x=\"%.2f\" y=\"%d\" text-anchor=\"middle\" font-size=\"13\" font-family=\"Arial\">%d</text>",
                    x, height - bottom + 23, entry.getKey()
            ));
        }

        svg.append(String.format(
                "<path d=\"%s\" fill=\"none\" stroke=\"#2563eb\" stroke-width=\"3\"/>",
                path
        ));

        svg.append("<text x=\"450\" y=\"530\" text-anchor=\"middle\" font-size=\"15\" font-family=\"Arial\">Text length (n)</text>");

        svg.append("<text x=\"25\" y=\"280\" text-anchor=\"middle\" transform=\"rotate(-90 25 280)\" font-size=\"15\" font-family=\"Arial\">Naive time / KMP time</text>");

        svg.append("</svg>");

        Path output = Path.of("results", "plots", "search_ratio.svg");
        Files.createDirectories(output.getParent());
        Files.writeString(output, svg.toString());

        System.out.println("Ratio plot saved to " + output);
    }

    private static double median(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        int n = sorted.size();

        if (n % 2 == 1) {
            return sorted.get(n / 2);
        }

        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }
}
