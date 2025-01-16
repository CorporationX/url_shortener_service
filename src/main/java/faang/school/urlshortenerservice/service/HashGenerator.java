package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.async.AsyncConfig;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62 base62;

    @Value("${sequence.batch.size}")
    private int batchSize;

    @Async(AsyncConfig.HASH_GENERATOR_POOL)
    @Transactional
    public void generateBatch() {
        List<Long> uniqueValues = hashRepository.getNextSequenceValues(batchSize);

        List<Hash> hashes = uniqueValues.stream()
                .map(this::generateHash)
                .toList();

        hashRepository.saveAll(hashes);
    }

    private Hash generateHash(long uniqueValue) {
        String salt = UUID.randomUUID().toString().substring(0, 8);
        String input = salt + uniqueValue;
        String hash = base62.encode(input.getBytes());
        return new Hash(hash);
    }
}
