package faang.school.urlshortenerservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("shedlock")
@Getter
@Setter
public class ShedLockProperties {
    private boolean enabled;
    private Defaults defaults;
    private Provider provider;

    @Getter
    @Setter
    public static class Defaults {
        private String lockAtMostFor;
    }

    @Getter
    @Setter
    public static class Provider {
        private Jdbc jdbc;
    }

    @Getter
    @Setter
    public static class Jdbc {
        private String tableName;
        private boolean useDbTime;
    }
}