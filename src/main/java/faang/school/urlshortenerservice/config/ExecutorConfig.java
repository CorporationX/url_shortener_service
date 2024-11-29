package faang.school.urlshortenerservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Configuration
public class ExecutorConfig {
    @Bean("hashGeneratorExecutor")
    public ExecutorService hashGeneratorExecutor() {
        return Executors.newSingleThreadExecutor();
    }
    @Bean("fillCacheExecutor")
    public ExecutorService fillCacheExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
