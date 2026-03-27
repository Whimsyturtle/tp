package seedu.address.model.statistics;

import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * Renders ASCII bar charts for statistics output.
 */
public final class AsciiBarChart {
    private static final String BAR_CHARACTER = "#";

    private final int maxBarWidth;

    /**
     * Constructs an {@code AsciiBarChart} with the specified maximum bar width.
     *
     * @param maxBarWidth Maximum width for each bar.
     */
    public AsciiBarChart(int maxBarWidth) {
        if (maxBarWidth <= 0) {
            throw new IllegalArgumentException("Bar width must be positive.");
        }
        this.maxBarWidth = maxBarWidth;
    }

    /**
     * Renders a percentage bar chart for the given entries.
     * Each line includes the label, a scaled bar, and a percentage label derived from {@code total}.
     *
     * @param entries Entries to render. Must not be null.
     * @param total Total used to compute percentages. Must be non-negative, but zero is allowed.
     * @return Rendered bar chart lines in entry order.
     */
    public List<String> renderPercentage(List<BarEntry> entries, int total) {
        requireNonNull(entries);
        if (total < 0) {
            throw new IllegalArgumentException("Total must be non-negative.");
        }

        int labelWidth = entries.stream()
                .map(BarEntry::getLabel)
                .mapToInt(String::length)
                .max()
                .orElse(0);

        return entries.stream()
                .map(entry -> {
                    double percent = total == 0 ? 0.0 : (entry.getValue() * 100.0 / total);
                    String percentageLabel = String.format("%.1f%% (%d)", percent, entry.getValue());
                    String bar = BAR_CHARACTER.repeat(scaleToWidth(entry.getValue(), total));
                    return String.format("%-" + labelWidth + "s | %s %s",
                            entry.getLabel(), bar, percentageLabel);
                })
                .toList();
    }

    /**
     * Renders a relative-count bar chart for the given entries.
     * Each line includes the label, a scaled bar, and a count label with the provided unit.
     *
     * @param entries Entries to render. Must not be null.
     * @param maxValue Maximum value used to scale bars. Must be non-negative, but zero is allowed.
     * @param unitLabel Unit label to append to each count. Must not be null.
     * @return Rendered bar chart lines in entry order.
     */
    public List<String> renderRelativeCounts(List<BarEntry> entries, int maxValue, String unitLabel) {
        requireNonNull(entries);
        requireNonNull(unitLabel);
        if (maxValue < 0) {
            throw new IllegalArgumentException("Max value must be non-negative.");
        }

        int labelWidth = entries.stream()
                .map(BarEntry::getLabel)
                .mapToInt(String::length)
                .max()
                .orElse(0);

        return entries.stream()
                .map(entry -> {
                    String bar = BAR_CHARACTER.repeat(scaleToWidth(entry.getValue(), maxValue));
                    String countLabel = String.format("%d %s", entry.getValue(), unitLabel);
                    return String.format("%-" + labelWidth + "s | %s %s",
                            entry.getLabel(), bar, countLabel);
                })
                .toList();
    }

    /**
     * Scales the given value into a bar width between 0 and {@code maxBarWidth}.
     * Ensures positive values still render a minimum bar length of 1 when {@code maxValue} is positive.
     */
    private int scaleToWidth(int value, int maxValue) {
        if (maxValue < 0) {
            throw new IllegalArgumentException("Max value must be non-negative.");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative.");
        }
        if (maxValue == 0 || value == 0) {
            return 0;
        }
        int length = (int) Math.round((value * 1.0 / maxValue) * maxBarWidth);
        length = Math.max(1, length);
        return Math.min(length, maxBarWidth);
    }
}
