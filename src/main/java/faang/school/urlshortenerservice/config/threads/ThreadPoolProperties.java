package faang.school.urlshortenerservice.config.threads;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("thread-pool")
public class ThreadPoolProperties {
    private int size;
    private int maxSize;
    private int timeout;
}
