package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;


@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.hash_range}")
    private int hashRange;

    @Transactional
    public List<Hash> generateBatch() {
        List<Hash> hashes = new ArrayList<>();
        List<Long> numbers = hashRepository.getUniqueNumbers(hashRange);
        log.info("Received {} unique numbers from hash repository", numbers.size());
        base62Encoder.encode(numbers).forEach(str -> hashes.add(new Hash(str)));
        log.info("{} numbers encode to base 62", numbers.size());
        List<Hash> list = hashRepository.saveAll(hashes);
        log.info("Hashes {} saved to hash repository", list.size());
        return list;
    }

    @Transactional
    public List<Hash> getHashes(int count) {
        List<String> hashes = hashRepository.getHashBatch(count);
        if (hashes.size() < count) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(count - hashes.size()));
        }
        return hashes.stream().map(Hash::new).toList();
    }

    @Async("cachedThreadPool")
    @Transactional
    public CompletableFuture<List<Hash>> generateBatchAsync(int count) {
        return CompletableFuture.completedFuture(getHashes(count));
    }

}
