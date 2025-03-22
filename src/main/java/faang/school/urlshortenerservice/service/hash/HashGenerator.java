package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.property.HashGeneratorProperty;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final HashGeneratorProperty hashGeneratorProperties;

    @Transactional
    public List<String> getHashes(int amount) {
        return hashRepository.pop(amount).stream().map(Hash::getHash).toList();
    }

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateHashes() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashGeneratorProperties.getBatchSize());

        hashRepository.save(base62Encoder.encode(uniqueNumbers));
    }

    @Transactional(readOnly = true)
    public boolean isMinimumThresholdExceeded() {
        int count = hashRepository.getSize();

        return count < hashGeneratorProperties.getMinLimit();
    }
}
