package faang.school.urlshortenerservice.config.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExecutorServiceConfig {

  @Value("${executor-service.core-pool-size}")
  private int corePoolSize;

  @Value("${executor-service.threads.number}")
  private int threadsNumber;

  @Bean
  public ExecutorService fixedThreadPool() {
    return Executors.newFixedThreadPool(threadsNumber);
  }

  @Bean
  public ExecutorService cachedThreadPool() {
    return Executors.newCachedThreadPool();
  }
}
