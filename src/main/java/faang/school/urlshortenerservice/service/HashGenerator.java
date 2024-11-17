package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${generator.batch.size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);

        List<String> hashes = base62Encoder.encode(uniqueNumbers);

        List<Hash> hashEntities = hashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashEntities);
        log.info("Generated and saved {} hashes", hashes.size());
    }
}
