package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.cache.capacity:10000}")
    private int cacheCapacity;

    @Transactional
    public void generateHashes() {
        List<Long> numsForHash = hashRepository.getUniqueNumbers(cacheCapacity);
        log.info("HashGenerator got a new list of numbers with size {} from DB.", numsForHash.size());
        List<Hash> hashes = base62Encoder.encode(numsForHash).stream()
                .map(Hash::new)
                .toList();
        log.info("HashGenerator got a new list of hashes with size {} from encoder.", hashes.size());
        hashRepository.saveAll(hashes);
        log.info("HashGenerator saved {} new hashes into DB.", hashes.size());
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getHashBatchAndDelete(amount);
        if (hashes.size() < amount) {
            generateHashes();
            hashes.addAll(hashRepository.getHashBatchAndDelete(amount - hashes.size()));
        }
        log.info("HashGenerator got a list of hashes with size {} from DB.", hashes.size());
        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }
}
