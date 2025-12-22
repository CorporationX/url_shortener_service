package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueIdRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final UniqueIdRepository uniqueIdRepository;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final EntityManager em;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private Integer batchSize;

    @Transactional
    public void generateAndSaveHashes(int count) {
        log.warn("Start generate hashes");
        long startTime = System.currentTimeMillis();

        List<Long> ids = uniqueIdRepository.getNextRange(count);

        List<Hash> buffer = new ArrayList<>(batchSize);

        for (Long id : ids) {
            buffer.add(Hash.builder()
                    .hashValue(base62Encoder.encode(id))
                    .build());

            if (buffer.size() == batchSize) {
                hashRepository.saveAll(buffer);
                hashRepository.flush();
                em.clear();
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            hashRepository.saveAll(buffer);
            hashRepository.flush();
            em.clear();
        }

        log.warn("Generated and saved {} hashes in {} ms",
                String.format("%,d", count),
                String.format("%,d", System.currentTimeMillis() - startTime));
    }
}
