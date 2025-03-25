package faang.school.urlshortenerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ExecutorServiceConfig {
    private static final int SCHEDULER_CORE_POOL_SIZE = 1;
    private static final int GENERATE_CORE_POOL_SIZE = 2;
    private static final int GENERATE_MAXIMUM_POOL_SIZE = 8;
    private static final int GENERATE_QUEUE_CAPACITY = 20;
    private static final long GENERATE_KEEP_ALIVE_TIME = 0L;
    private static final int GET_HASH_CORE_POOL_SIZE = 1;
    private static final int GET_HASH_MAXIMUM_POOL_SIZE = 1;
    private static final int GET_HASH_QUEUE_CAPACITY = 1;
    private static final long GET_HASH_KEEP_ALIVE_TIME = 0L;

    @Bean(name = "hashGenerateExecutorService")
    public ExecutorService hashGenerateExecutorService() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(GENERATE_QUEUE_CAPACITY);

        return new ThreadPoolExecutor(GENERATE_CORE_POOL_SIZE, GENERATE_MAXIMUM_POOL_SIZE, GENERATE_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardPolicy());
    }

    @Bean(name = "schedulerExecutorService")
    public ScheduledExecutorService schedulerExecutorService() {
        return new ScheduledThreadPoolExecutor(SCHEDULER_CORE_POOL_SIZE, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "getHasExecutorService")
    public ExecutorService getHashExecutorService() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(GET_HASH_QUEUE_CAPACITY);

        return new ThreadPoolExecutor(GET_HASH_CORE_POOL_SIZE, GET_HASH_MAXIMUM_POOL_SIZE, GET_HASH_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardPolicy());
    }
}
