package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${app.hash.batch-size.generation}")
    private int batchSize;

    @Transactional
    public void generateHashBatch() {
        generateHashBatch(batchSize);
    }

    @Transactional
    public void generateHashBatch(int amountOfGeneration) {
        // todo check new Transaction
        log.info("generateHashBatch, is transaction active: {}", TransactionSynchronizationManager.isActualTransactionActive());
        List<Long> sequenceValues = hashRepository.getUniqueNumbers(amountOfGeneration);
        Collections.shuffle(sequenceValues);
        List<String> generatedHashValues = base62Encoder.encodeBatch(sequenceValues);
        List<Hash> hashes = generatedHashValues.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
        log.info("{} hashes generated and inserted", hashes.size());
    }
}
