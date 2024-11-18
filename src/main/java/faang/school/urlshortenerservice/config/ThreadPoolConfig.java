package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${thread.generate_batch_executor.size}")
    private int generateBatchExecutorSize;

    @Value("${thread.generate_batch_executor.max_size}")
    private int generateBatchExecutorMaxSize;

    @Value("${thread.generate_batch_executor.task_queue.size}")
    private int generateBatchQueueSize;

    @Value("${thread.executor_service.size}")
    private int executorServiceSize;

    @Bean(name = "generateBatchExecutor")
    public Executor generateBatchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(generateBatchExecutorSize);
        executor.setMaxPoolSize(generateBatchExecutorMaxSize);
        executor.setQueueCapacity(generateBatchQueueSize);
        executor.setThreadNamePrefix("GenerateBatchExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "generateExecutorService")
    public ExecutorService generateExecutorService() {
        return Executors.newFixedThreadPool(executorServiceSize);
    }

}
