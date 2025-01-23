package faang.school.urlshortenerservice.model;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.batch.size}")
    private int batchSize;

    @Transactional
//    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        log.info("[{}] Starting hash generation.", Thread.currentThread().getName());

        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);

        log.info("[{}] Get unique numbers: {}", Thread.currentThread().getName(), uniqueNumbers);

        List<String> hashes = base62Encoder.encode(uniqueNumbers);

        log.info("[{}] Encoded hashes: {}", Thread.currentThread().getName(), hashes.size());

        hashRepository.saveAll(hashes.stream().map(Hash::new).toList());

        log.info("[{}] Hash generation completed.", Thread.currentThread().getName());
    }
}
