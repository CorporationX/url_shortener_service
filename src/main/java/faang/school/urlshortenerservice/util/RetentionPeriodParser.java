package faang.school.urlshortenerservice.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class RetentionPeriodParser {

    private final Pattern PERIOD_PATTERN = Pattern.compile("(\\d+)([yMd])");

    public Instant calculateExpiryDate(String retentionPeriod) {
        Matcher matcher = PERIOD_PATTERN.matcher(retentionPeriod);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Incorrect retention period format. Example: 1y, 2M, 3d");
        }

        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime result = switch (unit) {
            case "y" -> now.minusYears(value);
            case "M" -> now.minusMonths(value);
            case "d" -> now.minusDays(value);
            default -> throw new AssertionError("Unknown unit of time: " + unit);
        };

        return result.toInstant();
    }
}
