package faang.school.urlshortenerservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "url-shortener-service")
public class UrlShortenerProperties {

  private String baseUrl;
  private int hashBatchSize;
  private int uniqueNumbersCount;
  private long cacheSize;
  private int cacheLowThresholdPercent;

  private ThreadPoolConfig threadPool;

  @Getter
  @Setter
  public static class ThreadPoolConfig {

    private int coreSize;
    private int maxSize;
    private int queueCapacity;
  }
}