package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional()
    public void generateHash(int size) {
        hashRepository.saveAllBatch(generateAndGetHashes(size));
        log.info("Generating hashes completed, size = {} ", size);
    }

    @Transactional
    public List<String> getHashes(int batchSize) {
        List<String> hashes = hashRepository.getHashAndDeleteFromDb(batchSize);
        if (hashes.size() < batchSize) {
            hashes.addAll(generateAndGetHashes(batchSize - hashes.size()));
        }
        return hashes;
    }

    private List<String> generateAndGetHashes(int size) {
        log.info("generateAndGetHashes started, size {} ", size);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(size);
        if (CollectionUtils.isEmpty(uniqueNumbers)) {
            throw new RuntimeException("uniqueNumbers is not read");
        }
        return uniqueNumbers.stream()
                .map(base62Encoder::encode)
                .toList();
    }
}
