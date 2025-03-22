package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
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
public class HashCache {

  private final Queue<String> hashCache = new ConcurrentLinkedQueue<>();
  private final AtomicBoolean isUpdating = new AtomicBoolean(false);

  private final HashGenerator hashGenerator;
  private final ExecutorService executorService;
  private final UrlShortenerProperties properties;

  public String getHash() {
    if (isLessThanLowPercentage() && isUpdating.compareAndSet(false, true)) {
      log.info("A few hashes left ({}/{}), starting updating", hashCache.size(),
          properties.getCacheSize());
      executorService.submit(() ->
          hashGenerator.getHashes(properties.getCacheSize())
              .thenAccept(hashCache::addAll)
              .thenRun(() -> isUpdating.set(false)));
    }
    return hashCache.poll();
  }

  private boolean isLessThanLowPercentage() {
    return hashCache.size() / (properties.getCacheSize() / 100) < properties.getCacheLowPercent();
  }
}