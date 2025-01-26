package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.async.AsyncConfig;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.util.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62 base62;

    @Async(AsyncConfig.HASH_GENERATOR_POOL)
    @Transactional
    public void generateHashesAsync(int batchSize) {
        generateHashes(batchSize);
    }

    @Transactional
    public void generateHashes(int batchSize) {
        log.info("Hash generation has started");
        List<Long> uniqueValues = hashRepository.getNextSequenceValues(batchSize);

        List<Hash> hashes = uniqueValues.stream()
                .map(this::generateHash)
                .toList();

        hashRepository.saveAll(hashes);
    }

    private Hash generateHash(long uniqueValue) {
        String hash = base62.encode(uniqueValue);
        return new Hash(hash);
    }
}
