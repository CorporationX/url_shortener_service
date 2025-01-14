package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;

    private final EntityManager entityManager;

    private final Base62Encoder base62Encoder;

    @Value("${hash.range}")
    private int maxRange;

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);

        List<String> encodedHashes = base62Encoder.encode(range);

        List<Hash> hashes = encodedHashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAllAndFlush(hashes);
        entityManager.clear();
    }

}
