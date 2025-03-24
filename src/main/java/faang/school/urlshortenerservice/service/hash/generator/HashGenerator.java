package faang.school.urlshortenerservice.service.hash.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.service.encoder.Encoder;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final UrlShortenerProperties properties;
    private final Encoder encoder;
    private final HashService hashService;
    private final TransactionTemplate transactionTemplate;

    @PostConstruct
    public void init() {
        generateBatch();
    }

    public void generateBatch() {
        transactionTemplate.executeWithoutResult(status -> {
            List<Hash> newHashes = encoder.encodeList(getNewNumbers())
                    .stream()
                    .map(Hash::new)
                    .toList();

            log.info("Generated {} new Hashes", newHashes.size());
            hashService.saveHashesBatch(newHashes);
        });
    }

    private List<Long> getNewNumbers() {
        long maxAvailableBatch = properties.getMaxHashesInStore() - hashService.getHashesCount();
        return hashService.generateNewNumbers(Math.min(properties.getBatchSize(), maxAvailableBatch));
    }
}
