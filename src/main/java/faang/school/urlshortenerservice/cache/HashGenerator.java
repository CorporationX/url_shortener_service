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
import java.util.stream.LongStream;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${length.batchSize:250}")
    private int batchSize;

    private static final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Transactional
    @Scheduled(cron = "${hash.cron:0 0 0 * * *}")
    public void generateUniqueHash() {
        Set<Long> uniqueNumbers = LongStream.range(0, batchSize).boxed().collect(Collectors.toSet());
        Set<String> hashes = base62Encoder.encode(uniqueNumbers);
        Set<Hash> hashEntities = hashes.stream()
                .map(hash -> Hash.builder().base64Hash(hash).build())
                .collect(Collectors.toSet());
        hashRepository.saveAll(hashEntities);
    }

    @Transactional
    public Set<String> getHashes(long amount) {
        Set<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateUniqueHash();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getBase64Hash).collect(Collectors.toSet());
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Set<String>> getHashAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
