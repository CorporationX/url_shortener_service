package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RetentionPeriodParser {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("(\\d+)([yMd])");

    public LocalDateTime calculateExpiryDate(String retentionPeriod) {
        Matcher matcher = PERIOD_PATTERN.matcher(retentionPeriod);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Incorrect retention period format. Example: 1y, 2M, 3d");
        }

        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);

        LocalDateTime now = LocalDateTime.now();
        return switch (unit) {
            case "y" -> now.minusYears(value);
            case "M" -> now.minusMonths(value);
            case "d" -> now.minusDays(value);
            default -> throw new AssertionError("Unknown unit of time: " + unit);
        };
    }
}
