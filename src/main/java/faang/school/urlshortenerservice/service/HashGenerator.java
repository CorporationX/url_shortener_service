package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generator.batch-size}")
    private long maxRange;

    @Transactional
    @Async("hashGeneratorTaskExecutor")
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = base62Encoder.encoder(range).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }
}

