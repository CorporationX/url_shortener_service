package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlShortenerProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final UrlShortenerProperties urlShortenerProperties;
    private final AtomicBoolean uploadInProgressFlag = new AtomicBoolean();

    @Async("hashServiceExecutor")
    public CompletableFuture<Void> uploadHashInDatabaseIfNecessary() {
        if (!isEnoughHashCapacity() && uploadInProgressFlag.compareAndSet(false, true)) {
            List<Hash> hashes = generateBatch(urlShortenerProperties.hashAmountToGenerate());
            hashRepository.saveAll(hashes);
            uploadInProgressFlag.set(false);
            log.info("{} hashes added to database", hashes.size());
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async("hashServiceExecutor")
    public CompletableFuture<List<Hash>> getHashesFromDatabase() {
        return CompletableFuture.completedFuture(hashRepository.getHashes(urlShortenerProperties.hashAmountToLocalCache()));
    }

    private List<Hash> generateBatch(Long amountOfNumbersFromSequence) {
        List<Long> numbersToDecode = hashRepository.getUniqueNumbersFromSequence(amountOfNumbersFromSequence);

        return numbersToDecode.parallelStream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();
    }

    private boolean isEnoughHashCapacity() {
        long lowerBoundCapacity = (long) (urlShortenerProperties.hashAmountToGenerate() * urlShortenerProperties.hashDatabaseThresholdRatio());
        return hashRepository.count() >= lowerBoundCapacity;
    }
}
