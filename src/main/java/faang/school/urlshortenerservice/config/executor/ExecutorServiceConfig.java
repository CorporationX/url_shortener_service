package faang.school.urlshortenerservice.config.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    public Executor hashGeneratorExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public Executor getHashExecutor() {
        return Executors.newSingleThreadExecutor();
    }

}
