package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.managers.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repozitory.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${spring.url.hash.generator.max-range}")
    private int maxRange;

    @Value("${spring.url.hash.generator.hash-butch-size}")
    private int hashButchSize;

    @Async("hashGeneratorExecutor")
    @Transactional
    @Scheduled(cron = "${spring.url.hash.generator.cron}")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRange);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        List<Hash> hashEntities = hashes.stream()
                .map(hash -> new Hash(null, hash))
                .collect(Collectors.toList());
        hashRepository.saveAll(hashEntities);
    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<Hash>> getHashesAsync() {
        return CompletableFuture.completedFuture(getHashes());
    }

    @Transactional
    public List<Hash> getHashes() {
        List<Hash> hashes = hashRepository.getHashBatch(hashButchSize);
        if (hashes.size() < hashButchSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(hashButchSize));
        }
        return hashes;
    }
}
