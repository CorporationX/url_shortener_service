package faang.school.urlshortenerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AsyncConfiguration implements AsyncConfigurer {

    private final HashGeneratorAsyncConfig hashGeneratorAsyncConfig;

    @Bean(name = "hashGeneratorTaskExecutor")
    public Executor hashGeneratorTaskExecutor() {
        return getThreadPoolTaskExecutor(hashGeneratorAsyncConfig, "HashGenerator-");
    }

    @Override
    public Executor getAsyncExecutor() {
        return hashGeneratorTaskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    private ThreadPoolTaskExecutor getThreadPoolTaskExecutor(AsyncConfig asyncConfig, String threadNamePrefix) {
        var executor = new ThreadPoolTaskExecutor();
        var corePoolSize = getCorePoolSize(asyncConfig.getCorePoolSize());
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(getMaxPoolSize(asyncConfig.getMaxPoolSize(), corePoolSize));
        executor.setQueueCapacity(asyncConfig.getQueueCapacity());
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        return executor;
    }

    private static int getCorePoolSize(int corePoolSize) {
        return corePoolSize <= 0 ? Runtime.getRuntime().availableProcessors() + 1 : corePoolSize;
    }

    private static int getMaxPoolSize(int maxPoolSize, int corePoolSize) {
        return maxPoolSize <= 0 ? corePoolSize * 2 : maxPoolSize;
    }
}
