package faang.school.urlshortenerservice.config.shedlock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("shedlock")
public class ShedLockProperties {
    private boolean enabled;
    private Provider provider;
    private Defaults defaults;

    @Data
    public static class Defaults{
        private String lockAtMostFor;
    }

    @Data
    public static class Provider{
        private Jdbc jdbc;
    }

    @Data
    public static class Jdbc{
        private String tableName;
        private boolean useDbTime;
    }
}
