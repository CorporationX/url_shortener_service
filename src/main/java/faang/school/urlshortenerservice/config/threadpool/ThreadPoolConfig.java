package faang.school.urlshortenerservice.config.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean
    public ExecutorService generatorThreadPool() {
        return Executors.newCachedThreadPool();
    }
}
