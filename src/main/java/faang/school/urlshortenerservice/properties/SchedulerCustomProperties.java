package faang.school.urlshortenerservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool-setting.hashes-cleaner")
public record SchedulerCustomProperties(
        int size,
        int shutdownTimeoutSetting,
        boolean isWaitShutdown,
        String threadPrefix
) implements PoolProperties {}
