package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class UrlShorterConfig {

    @Value("${spring.hash-generator.max-generated-hashes:1000}")
    private int maxGeneratedHashes;

    @Bean
    public int maxGeneratedHashes() {
        return maxGeneratedHashes;
    }

    @Value("${spring.cache.url-hash-cache.capacity:5000}")
    private int urlHashCapacity;

    @Bean
    public int urlHashCacheCapacity() {
        return urlHashCapacity;
    }

    @Value("${spring.cache.url-hash-cache.exhaustion-percentage:20}")
    private int cacheExhaustionPercentage;

    @Bean
    public int cacheExhaustionPercentage() {
        return cacheExhaustionPercentage;
    }

    @Value("${spring.hash-generator.thread-pool-size}")
    private int generationThreadPoolSize;

    @Bean("hashGeneratorThreadPool")
    public ExecutorService hashGeneratorThreadPool() {
        return Executors.newFixedThreadPool(generationThreadPoolSize);
    }
}
