package faang.school.urlshortenerservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class ExecutorsConfig {
    @Bean
    public ExecutorService fillUpCacheExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService hashesGeneratorTaskExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
