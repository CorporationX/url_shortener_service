package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.unbrokendome.base62.Base62;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
  private final HashRepository hashRepository;

  @Async("threadPoolTaskExecutor")
  public void generateBatch() {
    List<Long> uniqueNumbers = hashRepository.getUniqueNumbers();
    List<String> encodedHashes = uniqueNumbers.stream()
        .map(Base62::encode)
        .toList();
    hashRepository.save(encodedHashes);
  }
}
