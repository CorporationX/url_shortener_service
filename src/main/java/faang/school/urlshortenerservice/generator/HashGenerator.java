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

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash.maxRange}")
    private int maxRange;

    @Transactional
    @Async(value = "hashGeneratorExecutor")
    public void generateHashes() { //TODO можно параллельность добавить.
        List<Long> range = hashRepository.getNextRange(maxRange);

        List<Hash> hashes = base62Encoder.encode(range).stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);

        log.info("Generated and saved {} hashes", hashes.size());
    }

    @Transactional
    public List<Hash> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHashes();
            hashes.addAll(getHashes(amount - hashes.size()));
        }
        return hashes;
    }
}
