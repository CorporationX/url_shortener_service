package faang.school.urlshortenerservice.config.async.hashgenerator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
@EnableConfigurationProperties(HashGeneratorAsyncProperties.class)
public class HashGeneratorAsyncConfig {

    @Bean(name = "hashGeneratorExecutor")
    public Executor hashGeneratorExecutor(HashGeneratorAsyncProperties props) {
        // Валидируем соотношение размеров пулов
        props.validatePoolSizes();
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(props.getCorePoolSize());
        executor.setMaxPoolSize(props.getMaxPoolSize());
        executor.setQueueCapacity(props.getQueueCapacity());
        executor.setThreadNamePrefix(props.getThreadNamePrefix());
        
        // Дополнительные потоки будут жить 60 секунд после простоя
        executor.setKeepAliveSeconds(60);
        executor.setAllowCoreThreadTimeOut(false);
        
        // Используем CallerRunsPolicy чтобы не терять задачи при переполнении
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(props.getShutdownTimeoutSeconds());
        
        executor.initialize();
        return executor;
    }
}

