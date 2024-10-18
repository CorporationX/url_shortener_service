package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.encoder.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class HashGenerator {
  private final Base62Encoder encoder;
  private final HashRepository hashRepository;
  private final int maxRange;

  public HashGenerator(Base62Encoder encoder,
                       HashRepository hashRepository,
                       @Value("${data.hash.generator.max_range:10000}") int maxRange) {
    this.encoder = encoder;
    this.hashRepository = hashRepository;
    this.maxRange = maxRange;
  }

  @Transactional
  @Scheduled(cron = "${data.hash.generator.cron:0 0 0 * * *}")
  public void generateBatch() {
    List<Long> range = hashRepository.getUniqueNumbers(maxRange);
    List<Hash> hashesToInsert = range.stream()
            .map(encoder::applyBase62Encoding)
            .map(Hash::new)
            .toList();

    hashRepository.saveAll(hashesToInsert);
  }

  @Transactional
  public List<String> getHashes(long amount) {
    List<Hash> hashes = hashRepository.findAndDelete(amount);
    if (hashes.size() < amount) {
      generateBatch();
      hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
    }
    return hashes.stream().map(Hash::getHash).toList();
  }

  @Transactional
  @Async("hashGeneratorExecutor")
  public CompletableFuture<List<String>> getHashesAsync(long amount) {
    List<Hash> hashes = hashRepository.findAndDelete(amount);
    if (hashes.size() < amount) {
      generateBatch();
      hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
    }
    return CompletableFuture.completedFuture(hashes.stream().map(Hash::getHash).toList());
  }
}