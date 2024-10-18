package faang.school.urlshortenerservice.service.generator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashCache {
  private final HashGenerator hashGenerator;
  private final AtomicBoolean filling;
  private final Queue<String> hashes;
  private final int capacity;
  private final int percentFill;
  private final int minValue;

  public HashCache(HashGenerator hashGenerator,
                   @Value("${data.hash.cache.capacity:10000}") int capacity,
                   @Value("${data.hash.cache.fill_percent:20}") int percentFill) {
    this.hashGenerator = hashGenerator;
    this.capacity = capacity;
    this.percentFill = percentFill;
    this.minValue = percentFill * capacity / 100;
    filling = new AtomicBoolean(false);
    hashes = new ArrayBlockingQueue<>(capacity);
  }

  @PostConstruct
  public void init() {
    hashes.addAll(hashGenerator.getHashes(capacity));
  }

  public String getHash() {
    if (hashes.size() <= minValue) {
      fillHashesAsync();
    }
    return hashes.poll();
  }

  private void fillHashesAsync() {
    if (filling.compareAndSet(false, true)) {
      hashGenerator.getHashesAsync((int) (capacity * (1 - percentFill / 100.0)))
              .thenAccept(hashes::addAll)
              .whenComplete((result, ex) -> {
                if (ex != null) {
                  log.error("Error getting hashes {}", ex.getMessage());
                }
                filling.set(false);
              });
    }
  }
}
