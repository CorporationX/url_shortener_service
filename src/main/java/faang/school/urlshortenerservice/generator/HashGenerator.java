package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62 base62;

    @Value("${hash.range:100}")
    private int maxRange;

    @Transactional
    @Scheduled(cron = "${hash.scheduled.cron}")
    public void generateHashBatch() {
        List<Long> range = hashRepository.getNextRange(maxRange);

        List<Hash> hashes = range.stream()
                .map(base62::encode)
                .map(Hash::new)
                .collect(Collectors.toList());

        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashBatch(int batchSize) {
        List<Hash> hashBatch = hashRepository.getAndDeleteHashBatch(batchSize);

        List<Hash> modifiableHashBatch = new ArrayList<>(hashBatch); // Без modifiableHashBatch вылетает UnsupportedOperationExceptio
        if(modifiableHashBatch.size() < batchSize) {
            generateHashBatch();
            modifiableHashBatch.addAll(hashRepository.getAndDeleteHashBatch(batchSize - modifiableHashBatch.size()));
        }

        return modifiableHashBatch.stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());
    }

    @Async("hashGeneratorTaskExecutor")
    public CompletableFuture<List<String>> getHashBatchAsync(int batchSize) {
        return CompletableFuture.completedFuture(getHashBatch(batchSize));
    }
}
