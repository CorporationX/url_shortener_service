package faang.school.urlshortenerservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thread-pool-setting.hashes-creator")
public record HashesCreatingProperties(
        int size,
        int shutdownTimeoutSetting,
        boolean isWaitShutdown,
        String threadPrefix
) {}
