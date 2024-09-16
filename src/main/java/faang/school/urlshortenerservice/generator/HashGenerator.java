package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.range}")
    private int maxRange;

    @Transactional
    public void generateHash() {
        var range = hashRepository.getNextRange();
        var hashes = encoder.encode(range);
        var testHash = hashes.stream().limit(10).toList();

        hashRepository.saveAll(testHash);
    }

    @Transactional
    public List<String> getHashes(long hashSize) {
        generateHash();
        log.info("Hash generated");
        return hashRepository.findAndDelete(hashSize);
    }

//    @Transactional
//    public List<String> getHashes(long hashSize) {
//        generateHash();
//        List<Hash> hashes = hashRepository.findAndDelete(hashSize);
//        if (hashes.size() < hashSize) {
//            generateHash();
//            hashes.addAll(hashRepository.findAndDelete(hashSize));
//        }
//        return hashes.stream().map(Hash::getHash).toList();
//    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
