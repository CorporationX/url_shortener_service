package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HashGenerator {

  private final HashRepository hashRepository;
  private final Base62Encoder base62Encoder;

  @Transactional
  @Async("threadPoolTaskExecutor")
  public void generateBatch() {
    List<Long> uniqueNumbers = hashRepository.getUniqueNumbers();
    List<String> encodedHashes = base62Encoder.encode(uniqueNumbers);

    hashRepository.save(encodedHashes);
  }

  @Transactional
  @Async("threadPoolTaskExecutor")
  public CompletableFuture<List<String>> getHashes(long amount) {
    List<String> hashes = hashRepository.findAndDelete(amount);
    if (hashes.size() < amount) {
      generateBatch();
      hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
    }
    return CompletableFuture.completedFuture(hashes);
  }
}
