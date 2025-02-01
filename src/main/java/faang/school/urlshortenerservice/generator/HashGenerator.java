package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
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
    @Value("${hash.hash.capacity:10000}")
    private int capacity;

    @Transactional
    public void generateHash() {
        log.info("Start generate hash number : {} ", capacity);
        List<Long> numbers = hashRepository.getUniqueNumbers(capacity);
        List<Hash> hashes = base62Encoder.encodeList(numbers)
                .stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        log.info("Get hash number : {} ", amount);
        List<Hash> hashes = hashRepository.getHashesAndDelete(amount);
        if (hashes.size() < amount) {
            log.info("There are not enough rows in a hash table: {} ", amount - hashes.size());
            generateHash();
            hashes.addAll(hashRepository.getHashesAndDelete(amount - hashes.size()));
        }

        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long number) {
        log.error("Start Async get hashes. Numbers: {}", number);
        return CompletableFuture.completedFuture(getHashes(number));
    }

    public String generateSingleHash() {
        log.error("System generated a single hash!");
        List<Long> numbers = hashRepository.getUniqueNumbers(1);
        return base62Encoder.encodeList(numbers).get(0);
    }
}