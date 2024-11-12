package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${thread.generate_batch_executor.size}")
    private int generateBatchExecutorSize;

    //TODO sadfgsdfg
    @Value("${thread.generate_batch_executor.max_size}")
    private int generateBatchExecutorMaxSize;

    @Value("${thread.generate_batch_executor.task_queue.size}")
    private int generateBatchQueueSize;

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

}
