package faang.school.urlshortenerservice.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "url")
@Component
public class LifecycleProperty {
    private String unit;
    private int value;

    public LocalDateTime getDeadLine()
    {
        ChronoUnit chronoUnit = parseChronoUnit(unit);
        return LocalDateTime.now().minus(value, chronoUnit);
    }

    private ChronoUnit parseChronoUnit(String unitStr) {
        switch (unitStr.toLowerCase()) {
            case "year":
            case "years":
                return ChronoUnit.YEARS;
            case "month":
            case "months":
                return ChronoUnit.MONTHS;
            case "day":
            case "days":
                return ChronoUnit.DAYS;
            case "hour":
            case "hours":
                return ChronoUnit.HOURS;
            case "minute":
            case "minutes":
                return ChronoUnit.MINUTES;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unitStr);
        }
    }
}
