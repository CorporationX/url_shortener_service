package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.UniqueHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final UniqueHashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${range_seq.range}")
    private int maxRange;

    @Transactional
    public void generateBatch() {
        List<Long> range = hashRepository.getNextRange(maxRange);
        List<Hash> hashes = base62Encoder.encode(range);
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        log.debug("Filling the local cache with hashes");
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.findAndDelete(amount-hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

}
