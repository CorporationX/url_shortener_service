package faang.school.urlshortenerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${hashgen.pool-size:4}")
    private int poolSize;

    @Bean(name = "hashGenExecutor")
    public Executor hashGenExecutor() {
        return Executors.newFixedThreadPool(poolSize, runnable -> {
            Thread t = new Thread(runnable);
            t.setName("HashGen-" + t.getId());
            return t;
        });
    }
}
