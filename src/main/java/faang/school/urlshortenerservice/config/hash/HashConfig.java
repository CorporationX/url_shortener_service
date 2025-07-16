package faang.school.urlshortenerservice.config.hash;

import io.seruco.encoding.base62.Base62;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.nio.ByteBuffer;

@EnableAsync
@Configuration
public class HashConfig {
    @Value("${hash.pool_size}")
    private int hashGeneratorPoolSize;

    @Bean
    public Base62 base62() {
        return Base62.createInstance();
    }

    @Bean
    public ByteBuffer byteBuffer() {
        return ByteBuffer.allocate(Long.BYTES);
    }

    @Bean
    public TaskExecutor hashGeneratorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
