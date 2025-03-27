package faang.school.urlshortenerservice.config.executor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties({GenerateHashExecutorProperties.class, ReceiveHashExecutorProperties.class})
public class ExecutorConfig {

    @Bean
    public ExecutorService executorService(ReceiveHashExecutorProperties props) {
        return new ThreadPoolExecutor(props.getCorePoolSize(),
                props.getMaxPoolSize(),
                props.getMaxPoolSize(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(props.getQueueCapacity()));
    }

    @Bean
    public ExecutorService generateExecutorService(GenerateHashExecutorProperties props) {
        return new ThreadPoolExecutor(props.getCorePoolSize(),
                props.getMaxPoolSize(),
                props.getMaxPoolSize(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(props.getQueueCapacity()));
    }
}