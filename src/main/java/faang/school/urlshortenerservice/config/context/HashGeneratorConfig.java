package faang.school.urlshortenerservice.config.context;

import faang.school.urlshortenerservice.properties.HashProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class HashGeneratorConfig {

    private final HashProperties hashProperties;

    @Bean(name = "hashGeneratorExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(hashProperties.getGenerator().getPool().getSize());
        executor.setMaxPoolSize(hashProperties.getGenerator().getPool().getSize());
        executor.setQueueCapacity(hashProperties.getGenerator().getQueueSize());
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }
}
