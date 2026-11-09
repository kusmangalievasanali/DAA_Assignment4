import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Generates the 7 plots from results/results.csv.
 * CSV columns (by position): algorithm,input,n,time_ms,counter,run
 * Header line (if any) is skipped automatically.
 */
public class PlotGenerator {

    record Row(String algo, String input, int n, double timeMs, long counter) {}

    static final Path CSV = Paths.get("results/results.csv");
    static final Path OUT = Paths.get("results/plots");

    static final Marker[] MARKERS = {
            SeriesMarkers.CIRCLE, SeriesMarkers.DIAMOND, SeriesMarkers.SQUARE, SeriesMarkers.TRIANGLE_UP
    };

    public static void main(String[] args) throws IOException {
        List<Row> rows = readCsv();
        Files.createDirectories(OUT);

        recursionCalls(rows);
        dpOperations(rows);
        dpMemory(rows);
        searchComparisons(rows);
        searchRatio(rows);
        dpTime(rows);
        searchTime(rows);

        System.out.println("Done. Plots saved to " + OUT.toAbsolutePath());
    }

    // ---------------------------------------------------------------- 1
    static void recursionCalls(List<Row> rows) throws IOException {
        XYChart logLog = chart("log-log", "n (string length, characters)",
                "recursive calls (count)", true, true, 700, 520);
        XYChart logY = chart("log Y, linear X", "n (string length, characters)",
                "recursive calls (count)", false, true, 700, 520);
        logLog.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        logY.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);

        String[] inputs = {"random", "different"};
        for (int i = 0; i < inputs.length; i++) {
            var data = agg(rows, "Recursive", inputs[i], r -> r.counter);
            addData(logLog, "Recursive - " + inputs[i], data, MARKERS[i], i);
            addData(logY, "Recursive - " + inputs[i], data, MARKERS[i], i);
        }
        save("recursion_calls.png",
                "Takeaway: recursive calls grow exponentially (a straight line on log Y); "
                        + "strings with no common characters cost about 28x more calls than random ones at n = 10.",
                logLog, logY);
    }

    // ---------------------------------------------------------------- 2
    static void dpOperations(List<Row> rows) throws IOException {
        XYChart c = chart("DP work: cell updates vs n", "n (length of both strings, characters)",
                "cell updates (count)", true, true, 1000, 600);
        var table = agg(rows, "TableDP", "random", r -> r.counter);
        var two = agg(rows, "TwoRowsDP", "random", r -> r.counter);
        addData(c, "TableDP (measured)", table, MARKERS[2], 0);
        addData(c, "TwoRowsDP (measured)", two, MARKERS[3], 1);
        addRef(c, "reference n", table.keySet(), n -> n);
        addRef(c, "reference n^2", table.keySet(), n -> n * n);
        save("dp_operations.png",
                "Takeaway: both DP versions do exactly n^2 cell updates (the two lines coincide) "
                        + "and follow the n^2 reference, not n.", c);
    }

    // ---------------------------------------------------------------- 3
    static void dpMemory(List<Row> rows) throws IOException {
        XYChart c = chart("DP memory: full table vs two rows (modelled)",
                "n (length of both strings, characters)", "memory for DP cells (KiB, int = 4 bytes)",
                true, true, 1000, 600);
        var ns = agg(rows, "TableDP", "random", r -> r.counter).keySet();
        TreeMap<Integer, Double> table = new TreeMap<>(), two = new TreeMap<>();
        for (int n : ns) {
            table.put(n, (n + 1.0) * (n + 1.0) * 4 / 1024.0);
            two.put(n, 2.0 * (n + 1.0) * 4 / 1024.0);
        }
        addData(c, "TableDP: (n+1)^2 cells", table, MARKERS[2], 0);
        addData(c, "TwoRowsDP: 2(n+1) cells", two, MARKERS[3], 0);
        save("dp_memory.png",
                "Takeaway: memory is modelled from the formulas (not measured): the table grows as n^2, two rows as n; "
                        + "at n = 1600 that is about 10 MiB against 12.5 KiB.", c);
    }

    // ---------------------------------------------------------------- 4
    static void searchComparisons(List<Row> rows) throws IOException {
        XYChart c = chart("String search: character comparisons vs text length",
                "n (text length, characters)", "character comparisons (count)", true, true, 1000, 600);
        String[][] combos = {
                {"NaiveSearch", "random"}, {"NaiveSearch", "adversarial"},
                {"KMP", "random"}, {"KMP", "adversarial"}};
        Collection<Integer> ns = null;
        for (int i = 0; i < combos.length; i++) {
            var data = agg(rows, combos[i][0], combos[i][1], r -> r.counter);
            ns = data.keySet();
            addData(c, combos[i][0] + " - " + combos[i][1], data, MARKERS[i < 2 ? 0 : 1], i % 2);
        }
        addRef(c, "reference n", ns, n -> n);
        addRef(c, "reference 20*n", ns, n -> 20 * n);
        save("search_comparisons.png",
                "Takeaway: on adversarial text Naive follows the 20*n line (pattern length about 20) while KMP stays near 2*n; "
                        + "on random text both stay close to n.", c);
    }

    // ---------------------------------------------------------------- 5
    static void searchRatio(List<Row> rows) throws IOException {
        XYChart c = chart("String search: comparisons per text character",
                "n (text length, characters)", "comparisons per character (ratio)", true, true, 1000, 600);
        String[][] combos = {
                {"NaiveSearch", "random"}, {"NaiveSearch", "adversarial"},
                {"KMP", "random"}, {"KMP", "adversarial"}};
        for (int i = 0; i < combos.length; i++) {
            var data = agg(rows, combos[i][0], combos[i][1], r -> (double) r.counter / r.n);
            addData(c, combos[i][0] + " - " + combos[i][1], data, MARKERS[i < 2 ? 0 : 1], i % 2);
        }
        save("search_ratio.png",
                "Takeaway: Naive needs about 20 comparisons per character on adversarial text, KMP never more than about 2, "
                        + "and on random text both need about 1.2-1.3.", c);
    }

    // ---------------------------------------------------------------- 6
    static void dpTime(List<Row> rows) throws IOException {
        XYChart c = chart("Edit distance DP: median running time",
                "n (length of both strings, characters)", "time (ms, median of 5 runs)", true, true, 1000, 600);
        String[] algos = {"TableDP", "TwoRowsDP"};
        String[] inputs = {"random", "different"};
        for (int a = 0; a < algos.length; a++)
            for (int i = 0; i < inputs.length; i++)
                addData(c, algos[a] + " - " + inputs[i],
                        agg(rows, algos[a], inputs[i], r -> r.timeMs), MARKERS[a + 2], i);
        save("dp_time.png",
                "Takeaway: for n >= 800 both versions take about 4x longer when n doubles (quadratic) and TwoRows is "
                        + "faster; small-n points are distorted by JIT warm-up.", c);
    }

    // ---------------------------------------------------------------- 7
    static void searchTime(List<Row> rows) throws IOException {
        XYChart c = chart("String search: median running time",
                "n (text length, characters)", "time (ms, median of 5 runs)", true, true, 1000, 600);
        String[][] combos = {
                {"NaiveSearch", "random"}, {"NaiveSearch", "adversarial"},
                {"KMP", "random"}, {"KMP", "adversarial"}};
        for (int i = 0; i < combos.length; i++)
            addData(c, combos[i][0] + " - " + combos[i][1],
                    agg(rows, combos[i][0], combos[i][1], r -> r.timeMs), MARKERS[i < 2 ? 0 : 1], i % 2);
        save("search_time.png",
                "Takeaway: on adversarial text Naive is about 9x slower than KMP at n = 64000; "
                        + "on random text the two are close and noisy.", c);
    }

    // =============================================================== helpers

    static List<Row> readCsv() throws IOException {
        List<Row> rows = new ArrayList<>();
        for (String line : Files.readAllLines(CSV)) {
            String[] p = line.trim().split(",");
            if (p.length < 5) continue;
            try {
                rows.add(new Row(p[0].trim(), p[1].trim(), Integer.parseInt(p[2].trim()),
                        Double.parseDouble(p[3].trim()), Long.parseLong(p[4].trim())));
            } catch (NumberFormatException e) {
                // header line or malformed row: skip
            }
        }
        if (rows.isEmpty()) throw new IllegalStateException("No data rows in " + CSV.toAbsolutePath());
        return rows;
    }

    /** n -> median of value over all runs for (algo, input). */
    static TreeMap<Integer, Double> agg(List<Row> rows, String algo, String input, ToDoubleFunction<Row> f) {
        Map<Integer, List<Double>> byN = rows.stream()
                .filter(r -> r.algo.equals(algo) && r.input.equals(input))
                .collect(Collectors.groupingBy(r -> r.n,
                        Collectors.mapping(f::applyAsDouble, Collectors.toList())));
        TreeMap<Integer, Double> out = new TreeMap<>();
        byN.forEach((n, vals) -> out.put(n, median(vals)));
        if (out.isEmpty()) System.err.println("WARNING: no rows for " + algo + " / " + input);
        return out;
    }

    static double median(List<Double> v) {
        List<Double> s = new ArrayList<>(v);
        Collections.sort(s);
        int k = s.size();
        return k % 2 == 1 ? s.get(k / 2) : (s.get(k / 2 - 1) + s.get(k / 2)) / 2.0;
    }

    static XYChart chart(String title, String xTitle, String yTitle,
                         boolean logX, boolean logY, int w, int h) {
        XYChart c = new XYChartBuilder().width(w).height(h)
                .title(title).xAxisTitle(xTitle).yAxisTitle(yTitle).build();
        var s = c.getStyler();
        s.setXAxisLogarithmic(logX);
        s.setYAxisLogarithmic(logY);
        s.setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        s.setMarkerSize(8);
        s.setLegendPosition(Styler.LegendPosition.OutsideE);
        s.setChartTitleFont(new Font("SansSerif", Font.BOLD, 16));
        return c;
    }

    /** styleIdx 0 = solid line, otherwise dash-dot (to tell the second input apart). */
    static void addData(XYChart c, String name, TreeMap<Integer, Double> data, Marker marker, int styleIdx) {
        double[] x = data.keySet().stream().mapToDouble(Integer::doubleValue).toArray();
        double[] y = data.values().stream().mapToDouble(Double::doubleValue).toArray();
        XYSeries s = c.addSeries(name, x, y);
        s.setMarker(marker);
        s.setLineStyle(styleIdx == 0 ? SeriesLines.SOLID : SeriesLines.DASH_DOT);
    }

    static void addRef(XYChart c, String name, Collection<Integer> ns, DoubleUnaryOperator f) {
        double[] x = ns.stream().mapToDouble(Integer::doubleValue).toArray();
        double[] y = Arrays.stream(x).map(f).toArray();
        XYSeries s = c.addSeries(name, x, y);
        s.setMarker(SeriesMarkers.NONE);
        s.setLineStyle(SeriesLines.DASH_DASH);
        s.setLineColor(Color.GRAY);
    }

    /** Puts one or more charts side by side and writes the caption under them. */
    static void save(String fileName, String caption, XYChart... charts) throws IOException {
        List<BufferedImage> imgs = new ArrayList<>();
        int w = 0, h = 0;
        for (XYChart c : charts) {
            BufferedImage im = BitmapEncoder.getBufferedImage(c);
            imgs.add(im);
            w += im.getWidth();
            h = Math.max(h, im.getHeight());
        }

        Font font = new Font("SansSerif", Font.ITALIC, 15);
        Graphics2D scratch = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();
        FontMetrics fm = scratch.getFontMetrics(font);
        List<String> lines = wrap(caption, fm, w - 40);
        scratch.dispose();

        int lineH = fm.getHeight() + 2;
        int capH = lines.size() * lineH + 24;

        BufferedImage out = new BufferedImage(w, h + capH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h + capH);
        int x = 0;
        for (BufferedImage im : imgs) {
            g.drawImage(im, x, 0, null);
            x += im.getWidth();
        }
        g.setColor(Color.BLACK);
        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int y = h + 16 + fm.getAscent();
        for (String ln : lines) {
            g.drawString(ln, 20, y);
            y += lineH;
        }
        g.dispose();

        ImageIO.write(out, "png", OUT.resolve(fileName).toFile());
        System.out.println("saved " + fileName);
    }

    static List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : text.split(" ")) {
            String trial = cur.length() == 0 ? word : cur + " " + word;
            if (fm.stringWidth(trial) > maxWidth && cur.length() > 0) {
                lines.add(cur.toString());
                cur = new StringBuilder(word);
            } else {
                cur = new StringBuilder(trial);
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines;
    }
}