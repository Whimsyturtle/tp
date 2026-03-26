package seedu.address.model.statistics;

import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * Represents a formatted statistics report ready for display.
 */
public final class StatisticsReport {
    private final String title;
    private final List<String> lines;

    /**
     * Constructs a {@code StatisticsReport} with the given title and lines.
     *
     * @param title Report title.
     * @param lines Report lines.
     */
    public StatisticsReport(String title, List<String> lines) {
        this.title = requireNonNull(title);
        this.lines = requireNonNull(lines);
    }

    /**
     * Returns the report as a single display string.
     */
    public String render() {
        if (lines.isEmpty()) {
            return title;
        }
        return title + "\n" + String.join("\n", lines);
    }
}
