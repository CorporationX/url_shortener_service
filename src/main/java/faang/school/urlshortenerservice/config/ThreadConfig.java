package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.repository.CustomHashRepositoryImpl;
import faang.school.urlshortenerservice.util.BatchProcessor;
import faang.school.urlshortenerservice.util.CustomBatchProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfig {

    @Bean(name = "hashGeneratorExecutor")
    public ThreadPoolTaskExecutor hashGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setThreadNamePrefix("HashGenerator-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashBatchProcessingExecutor")
    public ThreadPoolTaskExecutor hashBatchProcessingExecutor(@Value("${hash.processing.pool.size.core:5}") int corePoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setThreadNamePrefix("BatchProcessor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "hashBatchProcessor")
    public BatchProcessor hashBatchProcessor(@Value("${hash.processing.batch.divider:100}") int batchDivider, ThreadPoolTaskExecutor hashBatchProcessingExecutor, CustomHashRepositoryImpl customHashRepository) {
        return new CustomBatchProcessor(hashBatchProcessingExecutor, batchDivider);
    }
}