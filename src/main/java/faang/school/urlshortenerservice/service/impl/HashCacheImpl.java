package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.service.HashCache;
import jakarta.annotation.PostConstruct;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {

  private final Queue<String> freeHashes = new ConcurrentLinkedQueue<>();
  private final AtomicBoolean isCacheRefreshing = new AtomicBoolean(false);

  private final HashGeneratorImpl hashGenerator;
  private final ExecutorService executorService;
  private final UrlShortenerProperties properties;

  @PostConstruct
  public void init() {
    log.info("Initializing HashCache with size: {}", properties.getCacheSize());
    freeHashes.addAll(hashGenerator.getHashes(properties.getCacheSize()));
  }

  public String getHash() {
    String hash = freeHashes.poll();
    if (hash == null) {
      throw new IllegalStateException("No free hashes available in cache");
    }

    if (isLessThanLowPercentage() && isCacheRefreshing.compareAndSet(false, true)) {
      log.info("A few hashes left ({}/{}), starting updating", freeHashes.size(),
          properties.getCacheSize());
      executorService.execute(() -> {
            try {
              freeHashes.addAll(hashGenerator.getHashes(properties.getCacheSize()));
              isCacheRefreshing.set(false);
            } catch (Exception e) {
              log.error("Failed to refresh cache", e);
              isCacheRefreshing.set(false);
            }
          }
      );
    }
    return hash;
  }

  private boolean isLessThanLowPercentage() {
    return (freeHashes.size() * 100L) / properties.getCacheSize() <
        properties.getCacheLowThresholdPercent();
  }
}