package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.hash.Hash;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashService {

    @PersistenceContext
    private EntityManager entityManager;

    @Setter
    @Value("${value.setBatchSize}")
    private int batchSize;

    @Transactional
    public void saveHashes(List<String> hashList) {
        log.info("Starting to save {} hashes", hashList.size());

        IntStream.range(0, hashList.size()).boxed().collect(Collectors.groupingBy(i -> i / batchSize)).values()
                .forEach(batch -> {
                    log.debug("Processing batch of size {}", batch.size());
                    batch.forEach(i -> {
                        String hash = hashList.get(i);
                        Hash hashEntity = new Hash();
                        hashEntity.setHash(hash);
                        entityManager.persist(hashEntity);
                        log.debug("Persisted hash: {}", hash);
                    });
                    entityManager.flush();
                    entityManager.clear();
                    log.debug("Flushed and cleared EntityManager for batch");
                });

        entityManager.flush();
        entityManager.clear();
        log.info("Finished saving hashes");
    }
}
