package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Value("${app.hash-generator.executor.pool-size}")
    private int poolSize;
    @Value("${app.hash-generator.executor.queue-size}")
    private int queueSize;

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor() {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueSize);
        return new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, queue, new ThreadPoolExecutor.DiscardPolicy());
    }
}
