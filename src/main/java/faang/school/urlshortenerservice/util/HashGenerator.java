package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value( "${hash.range:100}")
    private int maxRange;

    @Async("hashThreadPool")
    public void generateBatch() {
        log.info("Generating new batch of hashes");
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<String> hashes = base62Encoder.encode(range);
        hashRepository.save(hashes);
    }


    @Transactional
    public List<String> getHashes(long amount) {
        log.info("Generating {} hashes", amount);
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }
}
