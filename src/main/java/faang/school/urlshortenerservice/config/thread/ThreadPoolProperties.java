package faang.school.urlshortenerservice.config.thread;


import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Setter
@Configuration
@ConfigurationProperties(prefix = "thread-pool-config")
public class ThreadPoolProperties {

    private int size;

    @Bean
    public ExecutorService urlThreadPool() {
        return Executors.newFixedThreadPool(size);
    }

}
