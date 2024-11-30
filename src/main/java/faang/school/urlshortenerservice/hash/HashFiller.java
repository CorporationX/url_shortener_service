package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashFiller {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public CompletableFuture<List<String>> fillHashCache(int batchSize) {
        hashGenerator.generateBatch();
        List<String> hashes = hashRepository.deleteByIdsAndGet(batchSize).stream()
            .map(Hash::getHash)
            .toList();
        if (hashes.isEmpty()) {
            log.warn("Hash is empty!!!");
            List<String> stringHashes = new ArrayList<>(hashGenerator.getStringHashes());
            return CompletableFuture.completedFuture(stringHashes);
        }
        log.info("{} hashes added to the queue", hashes);

        return CompletableFuture.completedFuture(hashes);
    }
}
