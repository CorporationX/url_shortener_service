package faang.school.urlshortenerservice.config.threadpool;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "threadpool")
public class ThreadPoolProperties {

    @Value("${hash.thread.pool.hash-size:2}")
    @NotNull(message = "Hash pool size must not be null")
    @Min(value = 1, message = "Hash pool size must be at least 1")
    private Integer hashPoolSize;

    @Value("${hash.thread.pool.core-size:2}")
    @NotNull(message = "Core pool size must not be null")
    @Min(value = 1, message = "Core pool size must be at least 1")
    private Integer coreSize;

    @Value("${hash.thread.pool.max-size:4}")
    @NotNull(message = "Max pool size must not be null")
    @Min(value = 1, message = "Max pool size must be at least 1")
    private Integer maxSize;

    @Value("${hash.thread.pool.queue-capacity:100}")
    @NotNull(message = "Queue capacity must not be null")
    @Min(value = 1, message = "Queue capacity must be at least 1")
    private Integer queueCapacity;

    @Value("${hash.thread.pool.keep-alive-seconds:60}")
    @NotNull(message = "Keep alive seconds must not be null")
    @Min(value = 1, message = "Keep alive seconds must be at least 1")
    private Integer keepAliveSeconds;

    @Value("${hash.thread.pool.thread-name-prefix}")
    @NotBlank(message = "Thread name prefix must not be blank")
    private String threadNamePrefix;

    @PostConstruct
    public void  validate() {
        log.info("Initializing hashTaskExecutor with coreSize={}, maxSize={}, queueCapacity={}, prefix={}",
                coreSize, maxSize, queueCapacity, threadNamePrefix);

        if (coreSize > maxSize) {
            log.error("Core pool size cannot be greater than max pool size");
            throw new IllegalArgumentException("Core pool size cannot be greater than max pool size");
        }
    }
}
