package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.base.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value(value = "${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private long batchSize;

    @Async("hashGeneratorPool")
    @Transactional
    public CompletableFuture<Void> generateBatch() {
        List<Long> nums = hashRepository.getUniqueNumbers(batchSize);
        List<String> stringHashes = base62Encoder.encode(nums);
        List<Hash> hashes = stringHashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
        return CompletableFuture.completedFuture(null);
    }
}
