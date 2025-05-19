package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generateHash.factor}")
    private int factor;


    @Transactional
    @Async("hashGenerationExecutor")
    public CompletableFuture<Void> generateHash(Long batchSize) {
        log.info("Generating batch of {} hashes", batchSize);

        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        log.info("Retrieved {} unique numbers", numbers.size());

        List<String> hashes = base62Encoder.encode(numbers);
        log.info("Generated {} hashes", hashes.size());

        hashRepository.save(hashes);
        log.info("Saved {} hashes to DB", hashes.size());

        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    public List<String> getHashes(Long amount) {
        List<String> hashes = hashRepository.getHashBatch(amount);
        log.info("Retrieved {} unique hashes", hashes.size());


        if (hashes.size() < amount) {
            log.warn("Недостаточно хэшей в БД ({} < {}), запрашиваем дополнительные...", hashes.size(), amount);
            CompletableFuture.runAsync(() -> generateHash(amount * factor));
            return hashes;
        }


        if (hashRepository.countAvailableHashes() < amount * 2) {
            log.info("Мало хэшей в БД, запускаем фоновую генерацию...");
            CompletableFuture.runAsync(() -> generateHash(amount * factor));
        }

        return hashes;
    }

    @Async("hashGenerationExecutor")
    @Transactional
    public CompletableFuture<List<String>> getHashesAsync(Long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}

