package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashBatchGenerator;
import faang.school.urlshortenerservice.service.HashGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {

  private final HashRepository hashRepository;
  private final HashBatchGenerator hashBatchGenerator;

  @Transactional
  public List<String> getHashes(long size) {
    List<String> hashes = hashRepository.findAndDelete(size);
    if (hashes.size() < size) {
      hashBatchGenerator.generateBatch(size);
      hashes.addAll(hashRepository.findAndDelete(size - hashes.size()));
    }
    return List.copyOf(hashes);
  }
}
