package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import faang.school.urlshortenerservice.service.HashBatchGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HashBatchGeneratorImpl implements HashBatchGenerator {

  private final HashRepository hashRepository;
  private final Base62Encoder base62Encoder;

  @Override
  @Transactional
  public void generateBatch(long size) {
    List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(); // Указываем размер
    List<String> encodedHashes = base62Encoder.encode(uniqueNumbers);
    hashRepository.save(encodedHashes);
  }
}
