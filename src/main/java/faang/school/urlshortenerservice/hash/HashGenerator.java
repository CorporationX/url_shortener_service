package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final UniqueNumberRepository uniqueNumberRepository;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    @Scheduled(cron = "${cache.cron:0 0 0 * * *}")
    public void generateBatch() {
        List<Long> uniqueNumbers = uniqueNumberRepository.getUniqueNumbers();
        List<String> hashes = uniqueNumbers.stream()
                .map(base62Encoder::encode)
                .toList();
        hashRepository.saveBatch(hashes);
    }

    @Transactional
    public List<String> getBatch(int uniqueNumbersBatch) {
        List<String> hashes = hashRepository.deleteAndGetBatch(uniqueNumbersBatch);
        if (hashes.size() < uniqueNumbersBatch) {
            generateBatch();
            hashes.addAll(hashRepository.deleteAndGetBatch(uniqueNumbersBatch - hashes.size()));
        }
        return hashes;
    }
}
