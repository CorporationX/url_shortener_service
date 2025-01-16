package faang.school.urlshortenerservice.config.async;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "async.thread-pools")
@Data
public class ThreadPoolProps {

    private ThreadPool hashGeneratorPool;

    @Data
    public static class ThreadPool {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadPrefixName;
    }
}
