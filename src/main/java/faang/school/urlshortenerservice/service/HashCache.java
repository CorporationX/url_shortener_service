package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashCache {

  @Value("${generator.hashes.min-level-percent}")
  private int minLevel;

  @Value("${generator.storage.size:100000}")
  private int capacity;

  @Value("${generator.batch.size:10000}")
  private int batchSize;

  private final AtomicBoolean isFilling = new AtomicBoolean(false);

  private BlockingQueue<String> localCache;

  private final HashRepository hashRepository;
  private final HashGenerator hashGenerator;
  private final ExecutorService cachedThreadPool;

  @PostConstruct
  public void init() {
    localCache = new LinkedBlockingQueue<>(capacity);
  }

  public String getHash() {
    addHashesAsync();
    try {
      return localCache.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public void addHashes() {
    if (isMinQtyReached()) {
      isFilling.set(true);
      List<String> hashes = hashRepository.takeHashBatch();
      List<List<String>> batches = splitIntoBatches(hashes);

      List<CompletableFuture<Void>> futures = batches.stream()
          .map(
              batch -> CompletableFuture.runAsync(() -> localCache.addAll(batch), cachedThreadPool))
          .toList();

      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
          .thenRun(() -> isFilling.set(false));
    }
    hashGenerator.generateBatch();
  }

  @Async("cachedThreadPool")
  public void addHashesAsync() {
    addHashes();
  }

  private boolean isMinQtyReached() {
    return 100 * localCache.size() / capacity < minLevel && !isFilling.get();
  }

  private List<List<String>> splitIntoBatches(List<String> hashes) {
    int totalSize = hashes.size();

    int batchNumbs = (totalSize + batchSize - 1) / batchSize;

    List<List<String>> batches = new ArrayList<>();

    for (int i = 0; i < batchNumbs; i++) {
      int start = i * batchSize;
      int end = Math.min(totalSize, (i + 1) * batchSize);
      batches.add(hashes.subList(start, end));
    }
    return batches;
  }

}
