package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    @Value("${thread.generate_batch_executor.size}")
    private int generateBatchExecutorSize;

    //TODO sadfgsdfg
    @Value("${thread.generate_batch_executor.max_size}")
    private int generateBatchExecutorMaxSize;

    @Value("${thread.generate_batch_executor.task_queue.size}")
    private int generateBatchQueueSize;

    @Bean(name = "generateBatchExecutor")
    public ExecutorService generateBatchExecutor() {
//        ExecutorService executorService = Executors.newFixedThreadPool(generateBatchExecutorSize,);
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(generateBatchExecutorSize, generateBatchExecutorMaxSize,
                60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(generateBatchQueueSize));
//        executor.setCorePoolSize(generateBatchExecutorSize);
//        executor.setMaxPoolSize(generateBatchExecutorMaxSize);
//        executor.setQueueCapacity(generateBatchQueueSize);
//        executor.setThreadNamePrefix("GenerateBatchExecutorThread-");
//        executor.initialize();
        return executor;
    }

}
