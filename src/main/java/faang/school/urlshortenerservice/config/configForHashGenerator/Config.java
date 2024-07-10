package faang.school.urlshortenerservice.config.configForHashGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class Config {

    @Value("${thread-pool.size}")
    private int poolSize;

    @Value("${queue.size}")
    private int queueSize;

    @Bean
    public ExecutorService getThreadPool() {
        return new ThreadPoolExecutor(poolSize,poolSize,0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(queueSize));
    }
}
