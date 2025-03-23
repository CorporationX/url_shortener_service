package faang.school.urlshortenerservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class UrlShortenerConfiguration {

    @Value("${url-shortener.thread-pool-size}")
    private int threadPoolSize;

    @Bean
    public ExecutorService schedulerThreadPool() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }

    @Bean
    public ExecutorService hashGeneratorThreadPool() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
