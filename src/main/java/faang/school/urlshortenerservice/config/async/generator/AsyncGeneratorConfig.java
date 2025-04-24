package faang.school.urlshortenerservice.config.async.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncGeneratorConfig {

    @Value("${async.generator.corePoolSize}")
    private int corePoolSize;

    @Value("${async.generator.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${async.generator.queueCapacity}")
    private int queueCapacity;

    @Value("${async.generator.keepAliveTime}")
    private int keepAliveTime;

    @Bean
    public ExecutorService hashGeneratorExecutorService() {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueCapacity));
    }
}