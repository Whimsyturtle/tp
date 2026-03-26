package seedu.address.model.statistics;

import static java.util.Objects.requireNonNull;

/**
 * Represents a labeled value for bar chart rendering.
 */
public final class BarEntry {
    private final String label;
    private final int value;

    /**
     * Constructs a {@code BarEntry} with the given label and value.
     *
     * @param label Label for the entry.
     * @param value Non-negative value for the entry.
     */
    public BarEntry(String label, int value) {
        this.label = requireNonNull(label);
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative.");
        }
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}
