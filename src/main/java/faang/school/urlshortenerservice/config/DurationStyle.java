package faang.school.urlshortenerservice.config;

import java.time.Duration;

public enum DurationStyle {
    YEARS {
        @Override
        public String print(Duration duration) {
            return (int) duration.toDays() / 365 + " YEAR";
        }
    },
    MONTHS {
        @Override
        public String print(Duration duration) {
            return (int) duration.toDays() / 30 + " MONTH";
        }
    },
    DAYS {
        @Override
        public String print(Duration duration) {
            return duration.toDays() + " DAY";
        }
    };

    public static DurationStyle detect(Duration duration) {
        long days = duration.toDays();
        if (days % 365 == 0) return YEARS;
        if (days % 30 == 0) return MONTHS;
        return DAYS;
    }

    public abstract String print(Duration duration);
}
