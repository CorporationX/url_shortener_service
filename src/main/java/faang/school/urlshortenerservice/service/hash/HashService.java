package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@RequiredArgsConstructor
public class HashService {

    @Value("${service.hash-service.batch-size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final HashMapper hashMapper;
    private final HashGenerator hashGenerator;

    @Transactional
    @Async("hashCacheFillerExecutor")
    public CompletableFuture<List<Hash>> getHashes(long amount) {
        log.info("Getting {} hashes from DB to cache", amount);
        List<Hash> hashes = hashRepository.getAndDeleteHashBatch(amount);
        if (hashes.size() < amount) {
            log.info("Not enough hashes in DB, generating more hashes");
            generateHashes(batchSize);
            log.info("Getting {} hashes from DB to cache", amount - hashes.size());
            hashes.addAll(hashRepository.getAndDeleteHashBatch(amount - hashes.size()));
        }
        return CompletableFuture.completedFuture(hashes);
    }

    @Transactional
    public void generateHashes(int batchSize) {
        log.info("Getting unique numbers for hashes from db, amount: {}", batchSize);
        List<Long> uniqueNumbers = getNextUniqueNumbers(batchSize);
        log.info("Generated {} unique numbers for hashes", uniqueNumbers.size());
        CompletableFuture<List<String>> hashes = hashGenerator.generateHashBatch(uniqueNumbers);
        log.info("Saving generated hashes to database");
        List<String> actualHashes = hashes.join();
        saveHashes(actualHashes);
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        hashRepository.saveAll(hashMapper.toEntity(hashes));
    }

    @Transactional
    public List<Long> getNextUniqueNumbers(int count) {
        return hashRepository.getNextUniqueNumbers(count);
    }

    public void saveCleanedHashesToDatabase(List<String> hashes) {
        log.info("Saving cleaned hashes to database");
        List<Hash> cleanedHashes = hashMapper.toEntity(hashes);
        hashRepository.saveAll(cleanedHashes);
    }
}
