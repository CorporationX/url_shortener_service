package faang.school.urlshortenerservice.config.context;

import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class SpringAsyncConfig {

  private final UrlShortenerProperties properties;

  @Bean(name = "threadPoolTaskExecutor")
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(properties.getThreadPool().getCoreSize());
    executor.setMaxPoolSize(properties.getThreadPool().getMaxSize());
    executor.setQueueCapacity(properties.getThreadPool().getQueueCapacity());
    executor.setThreadNamePrefix("AsyncExecutor-");
    executor.initialize();

    return executor;
  }
}