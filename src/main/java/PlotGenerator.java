
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class PlotGenerator {

    private record Point(int n, double value) {}

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(
                Path.of("results", "results.csv")
        );

        Path folder = Path.of("results", "plots");
        Files.createDirectories(folder);

        plot(lines, folder.resolve("edit_distance.svg"),
                "Edit Distance - Median Time",
                "random", "time",
                new String[]{"TableDP", "TwoRowsDP"});

        plot(lines, folder.resolve("search_time.svg"),
                "String Search - Adversarial Time",
                "adversarial", "time",
                new String[]{"NaiveSearch", "KMP"});

        plot(lines, folder.resolve("search_comparisons.svg"),
                "String Search - Comparisons",
                "adversarial", "operations",
                new String[]{"NaiveSearch", "KMP"});

        System.out.println("Three plots saved to results/plots");
    }

    private static void plot(
            List<String> lines,
            Path output,
            String title,
            String input,
            String metric,
            String[] algorithms
    ) throws IOException {

        int width = 900;
        int height = 550;
        int left = 100;
        int right = 50;
        int top = 75;
        int bottom = 90;

        String[] colors = {"#2563eb", "#dc2626"};

        List<List<Point>> series = new ArrayList<>();
        double maxX = 0;
        double maxY = 0;

        for (String algorithm : algorithms) {
            Map<Integer, List<Double>> values = new TreeMap<>();

            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");

                if (!parts[0].equals(algorithm)
                        || !parts[1].equals(input)) {
                    continue;
                }

                int n = Integer.parseInt(parts[2]);
                double value = Double.parseDouble(
                        parts[metric.equals("time") ? 3 : 4]
                );

                values.computeIfAbsent(
                        n, k -> new ArrayList<>()
                ).add(value);
            }

            List<Point> points = new ArrayList<>();

            for (Map.Entry<Integer, List<Double>> entry
                    : values.entrySet()) {

                List<Double> samples = entry.getValue();
                Collections.sort(samples);

                double median = samples.get(samples.size() / 2);
                points.add(new Point(entry.getKey(), median));

                maxX = Math.max(maxX, entry.getKey());
                maxY = Math.max(maxY, median);
            }

            series.add(points);
        }

        maxY = Math.max(maxY * 1.1, 0.000001);

        StringBuilder svg = new StringBuilder();

        svg.append(String.format(
                Locale.US,
                "<svg xmlns=\"http://www.w3.org/2000/svg\" " +
                        "width=\"%d\" height=\"%d\">",
                width, height
        ));

        svg.append("<rect width=\"100%\" height=\"100%\" fill=\"white\"/>");

        svg.append(String.format(
                "<text x=\"450\" y=\"35\" text-anchor=\"middle\" " +
                        "font-size=\"23\" font-family=\"Arial\">%s</text>",
                title
        ));

        svg.append(String.format(
                "<path d=\"M%d %d V%d H%d\" " +
                        "fill=\"none\" stroke=\"black\" stroke-width=\"2\"/>",
                left, top, height - bottom, width - right
        ));

        for (int tick = 0; tick <= 5; tick++) {
            double value = maxY * tick / 5.0;
            double y = height - bottom
                    - (height - top - bottom) * tick / 5.0;

            svg.append(String.format(
                    Locale.US,
                    "<text x=\"%d\" y=\"%.1f\" text-anchor=\"end\" " +
                            "font-size=\"13\" font-family=\"Arial\">%.3f</text>",
                    left - 12, y + 5, value
            ));
        }

        for (int s = 0; s < series.size(); s++) {
            StringBuilder path = new StringBuilder();

            for (int i = 0; i < series.get(s).size(); i++) {
                Point p = series.get(s).get(i);

                double x = left
                        + p.n / maxX * (width - left - right);

                double y = height - bottom
                        - p.value / maxY * (height - top - bottom);

                path.append(String.format(
                        Locale.US,
                        "%s %.2f %.2f ",
                        i == 0 ? "M" : "L", x, y
                ));

                svg.append(String.format(
                        Locale.US,
                        "<circle cx=\"%.2f\" cy=\"%.2f\" r=\"5\" fill=\"%s\"/>",
                        x, y, colors[s]
                ));

                svg.append(String.format(
                        Locale.US,
                        "<text x=\"%.2f\" y=\"%d\" text-anchor=\"middle\" " +
                                "font-size=\"12\" font-family=\"Arial\">%d</text>",
                        x, height - bottom + 23, p.n
                ));
            }

            svg.append(String.format(
                    "<path d=\"%s\" fill=\"none\" stroke=\"%s\" " +
                            "stroke-width=\"3\"/>",
                    path, colors[s]
            ));

            svg.append(String.format(
                    "<text x=\"%d\" y=\"%d\" font-size=\"15\" " +
                            "font-family=\"Arial\" fill=\"%s\">%s</text>",
                    130 + s * 250, height - 25,
                    colors[s], algorithms[s]
            ));
        }

        String yLabel = metric.equals("time")
                ? "Time (ms)"
                : "Character comparisons";

        svg.append(String.format(
                "<text x=\"450\" y=\"530\" text-anchor=\"middle\" " +
                        "font-size=\"15\" font-family=\"Arial\">Input size (n)</text>"
        ));

        svg.append(String.format(
                "<text x=\"25\" y=\"280\" text-anchor=\"middle\" " +
                        "transform=\"rotate(-90 25 280)\" " +
                        "font-size=\"15\" font-family=\"Arial\">%s</text>",
                yLabel
        ));

        svg.append("</svg>");

        Files.writeString(output, svg.toString());
    }
}
