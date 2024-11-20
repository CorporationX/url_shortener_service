package faang.school.urlshortenerservice.util.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.cache.hash.HashCacheProperty;
import faang.school.urlshortenerservice.util.encoder.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Encoder encoder;
    private final HashCacheProperty hashCacheProperty;

    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashCacheProperty.getAmountHash());
        List<Hash> hashes = encoder.encodeBatch(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }
}
