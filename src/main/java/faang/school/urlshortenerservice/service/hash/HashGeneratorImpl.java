package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.property.HashGeneratorProperty;
import faang.school.urlshortenerservice.repository.api.HashRepository;
import faang.school.urlshortenerservice.service.hash.api.HashGenerator;
import faang.school.urlshortenerservice.util.api.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashGeneratorProperty hashGeneratorProperties;

    @Transactional
    @Override
    public List<String> getHashes(int amount) {
        return hashRepository.popBatch(amount)
            .stream()
            .map(Hash::getHash)
            .toList();
    }

    @Async("hashGeneratorExecutor")
    @Transactional
    @Override
    public void generateHashes() {
        log.info("Hashes are being generated...");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashGeneratorProperties.getBatchSize());

        hashRepository.save(base62Encoder.encodeNumbers(uniqueNumbers));
        log.info("Hashes have been created");
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isMinimumThresholdExceeded() {
        int count = hashRepository.getSize();

        return count < hashGeneratorProperties.getMinLimit();
    }
}
