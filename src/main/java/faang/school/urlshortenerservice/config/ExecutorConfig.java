package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

  @Bean
  public ExecutorService hashCacheExecutor(UrlShortenerProperties properties) {
    return new ThreadPoolExecutor(
        properties.getThreadPool().getCoreSize(),
        properties.getThreadPool().getMaxSize(),
        60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(properties.getThreadPool().getQueueCapacity()),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
  }
}