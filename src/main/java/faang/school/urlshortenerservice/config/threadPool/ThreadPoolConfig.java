package faang.school.urlshortenerservice.config.threadPool;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolProperties properties;

    @Bean
    public ThreadPoolExecutor shortLinkThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getQueueCapacity())
        );
    }
}
