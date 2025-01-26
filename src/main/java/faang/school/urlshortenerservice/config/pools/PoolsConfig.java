package faang.school.urlshortenerservice.config.pools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PoolsConfig {

    @Value("${pools.generateHashesPool.size}")
    private int generateHashesSize;
    @Value("${pools.redisSaveUrl.size}")
    private int redisSaveUrlSize;

    @Bean
    public ExecutorService generateHashesPool() {
        return Executors.newFixedThreadPool(generateHashesSize);
    }
    @Bean
    public ExecutorService redisSaveUrlPool() {
        return Executors.newFixedThreadPool(redisSaveUrlSize);
    }
}
