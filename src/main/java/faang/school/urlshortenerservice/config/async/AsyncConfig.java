package faang.school.urlshortenerservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {

    @Value("${executor.threads_number}")
    private int threadsNumber;
    @Value("${executor.task_queue_size}")
    private int taskQueueSize;

    @Bean
    public ExecutorService executor() {
        return new ThreadPoolExecutor(threadsNumber, threadsNumber, 0L,
                TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(taskQueueSize));
    }
}
