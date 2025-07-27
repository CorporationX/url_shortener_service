package faang.school.urlshortenerservice.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfiguration {

    private final ExecutorConfig executorConfig;
    @Bean(name = "executorForBase62")
    public Executor customExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(executorConfig.getCore());
        taskExecutor.setMaxPoolSize(executorConfig.getMax());
        taskExecutor.setThreadNamePrefix(executorConfig.getPrefix());
        taskExecutor.setQueueCapacity(executorConfig.getQueue());
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
}
