package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {

  @Value("${generator.unique-ids}")
  private int n;

  private final HashRepository hashRepository;
  private final Base62Encoder base62Encoder;

  @Async("cachedThreadPool")
  @Transactional
  @Override
  public void generateBatch() {
    List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);
    List<String> hashes = base62Encoder.encode(uniqueNumbers);
    hashRepository.save(hashes);
  }
}
