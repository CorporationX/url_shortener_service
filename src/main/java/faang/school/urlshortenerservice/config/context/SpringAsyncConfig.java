package faang.school.urlshortenerservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class SpringAsyncConfig implements AsyncConfigurer {

    @Value("${url-shortener-service.async.hash.maxPoolSize}")
    private int hashMaxPoolSize;
    @Value("${url-shortener-service.async.hash.corePoolSize}")
    private int hashCorePoolSize;
    @Value("${url-shortener-service.async.hash.queueCapacity}")
    private int hashQueueCapacity;
    @Value("${url-shortener-service.async.cache.maxPoolSize}")
    private int cacheMaxPoolSize;
    @Value("${url-shortener-service.async.cache.corePoolSize}")
    private int cacheCorePoolSize;
    @Value("${url-shortener-service.async.cache.queueCapacity}")
    private int cacheQueueCapacity;

//    @Bean("hashThreadPool")
//    public Executor hashThreadPool() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//
//        executor.setCorePoolSize(hashCorePoolSize);
//        executor.setMaxPoolSize(hashMaxPoolSize);
//        executor.setQueueCapacity(hashQueueCapacity);
//        executor.initialize();
//        return executor;
//    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(cacheCorePoolSize);
        executor.setMaxPoolSize(cacheMaxPoolSize);
        executor.setQueueCapacity(cacheQueueCapacity);
        executor.initialize();
        return executor;
    }

//    @Bean(name = "cacheThreadPool")
//    public Executor cacheThreadPool() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//
//        executor.setCorePoolSize(cacheCorePoolSize);
//        executor.setMaxPoolSize(cacheMaxPoolSize);
//        executor.setQueueCapacity(cacheQueueCapacity);
//
//        executor.initialize();
//        return executor;
//    }
}