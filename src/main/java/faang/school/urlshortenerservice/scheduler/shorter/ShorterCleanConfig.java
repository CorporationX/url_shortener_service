package faang.school.urlshortenerservice.scheduler.shorter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "scheduler.expired-urls-clean")
@Configuration
public class ShorterCleanConfig {

    private String cron;
    private int batchSize;
    private int fetchLimit;
    private ExecutorConfig executorConfig = new ExecutorConfig();

    @Getter
    @Setter
    public static class ExecutorConfig {
        private int corePoolSize;
        private int maxPoolSize;
        private String threadNamePrefix;
    }
}