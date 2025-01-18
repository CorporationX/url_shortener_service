package faang.school.urlshortenerservice.config.async.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class HashGeneratorExecutor {

    @Value("${hash.generation.thread-pool}")
    private int fixedThreadPool;

    @Bean
    public ExecutorService hashGenerationExecutor() {
        return Executors.newFixedThreadPool(fixedThreadPool);
    }
}
