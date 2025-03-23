package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.encoder.Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {

    private final HashRepository hashRepository;
    private final Encoder encoder;
    private final Executor asyncTaskExecutor;
    private final ShortenerProperties shortenerProperties;

    @Override
    public List<Hash> generateHashes(int size) {
        log.debug("Generation of new {} caches", size);
        List<Long> sequenceNumbers = hashRepository.getUniqueNumbers(size);
        List<String> encodedHashes = encoder.encode(sequenceNumbers);
        return encodedHashes.stream()
                .map(Hash::new)
                .toList();
    }

    @Transactional
    public void saveHashes(List<Hash> hashes) {
        int batchSize = shortenerProperties.hashesBatchSize();
        ListUtils.partition(hashes, batchSize).forEach(hashRepository::saveAll);
        log.info("Hashes was saved to database. Hashes quantity: {}", hashes.size());
    }

    public List<Hash> readFreeHashes(int quantity) {
        log.info("Reading {} free hashes from database", quantity);
        List<Hash> hashes = hashRepository.getFreeHashesBatch(quantity);
        int hashesSize = hashes.size();
        int additionalQuantity = quantity - hashesSize;
        if (hashesSize < quantity) {
            saveHashes(generateHashes(additionalQuantity));
            List<Hash> additionalHashes = hashRepository.getFreeHashesBatch(additionalQuantity);
            hashes.addAll(additionalHashes);
        }
        return hashes;
    }
}
