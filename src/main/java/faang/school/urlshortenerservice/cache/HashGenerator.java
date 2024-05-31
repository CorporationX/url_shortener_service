package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${length.range:3}")
    private int length;

    @Value("${length.batchSize:100}")
    private int batchSize;

    @Value("${length.n}")
    private int n;

    @Async("hashGeneratorExecutor")
    @Transactional
    @Scheduled(cron = "${expression.cron}")
    public void generateBatch() {
        Set<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);
        Set<String> hashes = base62Encoder.encode(uniqueNumbers);
        Set<Hash> hashEntities = hashes.parallelStream()
                .map(hash -> Hash.builder().hash(hash).build())
                .collect(Collectors.toSet());
        hashRepository.saveAll(hashEntities);
    }

    @Transactional
    public Set<String> getHashes(long amount) {
        Set<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).collect(Collectors.toSet());
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Set<String>> getHashAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
