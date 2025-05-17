package faang.school.urlshortenerservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool-setting.queue-refilling")
public record QueueRefillingProperties(
        int size,
        int shutdownTimeoutSetting,
        boolean isWaitShutdown,
        String threadPrefix
) implements PoolProperties {}
