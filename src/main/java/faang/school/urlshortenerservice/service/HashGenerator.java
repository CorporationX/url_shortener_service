package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private static final int HASH_GENERATION_LOCK_ID = 17;

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;

    public List<String> generateHashes(int count) {
        return base62Encoder.encodeBatch(getNextSequenceBatch(count));
    }

    @Transactional
    public void refillHashStorage(int count) {
        boolean successfullyLockedByCurrentThread = hashRepository.tryLock(HASH_GENERATION_LOCK_ID);

        if (!successfullyLockedByCurrentThread) {
            log.info("Another instance is already refilling storage. Skipping...");
            return;
        }

        try {
            log.info("Start refiling hash storage...");
            long start = System.currentTimeMillis();

            List<Hash> newHashes = generateHashes(count).stream()
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(newHashes);
            log.info("{} hashes generated and saved in {} millis", newHashes.size(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("Error during storage refill", e);
        } finally {
            hashRepository.unlock(HASH_GENERATION_LOCK_ID);
        }
    }

    private List<Long> getNextSequenceBatch(int count) {
        List<Long> sequence = hashRepository.getNextSequenceBatchValues(count);
        Collections.shuffle(sequence);
        return sequence;
    }
}