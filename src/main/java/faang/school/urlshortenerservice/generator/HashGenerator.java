package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashGeneratorProperties properties;
    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional()
    public void generateHash() {
        log.info("Generating hashes started");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(properties.getBatchSize());
        if ((uniqueNumbers == null) || uniqueNumbers.isEmpty()) {
            throw new RuntimeException("uniqueNumbers is not read");
        }

        uniqueNumbers.stream()
                .map(number -> new Hash(base62Encoder.encodeSingle(number)))
                .forEach(entityManager::persist);

        entityManager.flush();
        entityManager.clear();
        log.info("Generating hashes completed");
    }

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes;
    }
}
