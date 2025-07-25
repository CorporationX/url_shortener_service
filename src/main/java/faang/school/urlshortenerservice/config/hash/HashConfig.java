package faang.school.urlshortenerservice.config.hash;

import io.seruco.encoding.base62.Base62;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.nio.ByteBuffer;

@Configuration
public class HashConfig {
    @Bean
    public Base62 base62() {
        return Base62.createInstance();
    }

    @Bean
    public ByteBuffer byteBuffer() {
        return ByteBuffer.allocate(Long.BYTES);
    }

    @Bean
    public TaskExecutor hashGeneratorPool(HashProperties hashProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(hashProperties.poolSize());
        executor.setQueueCapacity(hashProperties.queueSize());
        executor.initialize();
        return executor;
    }
}
