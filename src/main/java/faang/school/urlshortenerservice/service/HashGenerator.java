package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import java.util.List;
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
}
