package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class DefaultConfig {
    @Bean
    public Queue<Hash> queue(){
        return new ConcurrentLinkedDeque<>();
    }

    @Bean
    public AtomicBoolean hashGeneratorAtomicBoolean(){
        return new AtomicBoolean(true);
    }

    @Bean
    public AtomicBoolean localHashAtomicBoolean(){
        return new AtomicBoolean(true);
    }

    @Bean
    public ExecutorService executorService(){
       return Executors.newFixedThreadPool(3);
    }
}
