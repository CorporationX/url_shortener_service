package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
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
        log.info("Generating hashes started");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(size);
        if (CollectionUtils.isEmpty(uniqueNumbers)) {
            throw new RuntimeException("uniqueNumbers is not read");
        }

        List<Hash> hashes = uniqueNumbers.stream()
                .map(number -> new Hash(Base62Encoder.encode(number)))
                .toList();

        hashRepository.saveAll(hashes);
        log.info("Generating hashes completed");
    }

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHash(amount - hashes.size());
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes;
    }
}
