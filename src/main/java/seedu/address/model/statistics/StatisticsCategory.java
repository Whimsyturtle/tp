package seedu.address.model.statistics;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the supported statistics categories.
 */
public enum StatisticsCategory {
    ROLE("role"),
    RECORD("record");

    private final String token;

    StatisticsCategory(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    /**
     * Parses the given {@code token} into a {@code StatisticsCategory} case insensitively, if any.
     */
    public static Optional<StatisticsCategory> fromToken(String token) {
        return Arrays.stream(values())
                .filter(category -> category.token.equalsIgnoreCase(token))
                .findFirst();
    }
}
