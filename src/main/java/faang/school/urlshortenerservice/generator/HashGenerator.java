package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashJpaRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashJpaRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range.max}")
    private int maxRange;

    @Transactional
    @Async("executorgenerator")
    public void generateBatch() {
        List<Long> range = getUniqueNumbers();
        List<String> hashes = base62Encoder.applyBase62Encoding(range);
        log.info("hashes = {} ",hashes.size());
        hashRepository.saveAll(hashes.stream().map(Hash::new).toList());
    }
    private List<Long> getUniqueNumbers() {
        return hashRepository.getNextRange(maxRange);
    }

    @Transactional
    public List<String> getHashBatch(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }
}