package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;

    @Transactional()
    public void generateHash(int size) {
        hashRepository.saveAll(generateAndGetHashes(size));
        log.info("Generating hashes completed, size = {} ", size);
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            hashes.addAll(generateAndGetHashes(amount - hashes.size()));
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
                .map(Base62Encoder::encode)
                .toList();
    }
}
