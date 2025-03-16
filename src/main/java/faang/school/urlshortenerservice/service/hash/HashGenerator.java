package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.service.encoder.Encoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final UrlShortenerProperties properties;
    private final Encoder encoder;
    private final HashService hashService;

    @PostConstruct
    public void init() {
        generateBatch();
    }

    @Async("hashGeneratorThreadPool")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateBatch() {
        log.info("Generate hashes for batch");

        long maxAvailableBatch = properties.getMaxHashesInStore() - hashService.getHashesCount();
        List<Long> newNumbers = hashService.getNewNumbers(Math.min(properties.getBatchSize(), maxAvailableBatch));

        List<Hash> hashes = encoder.encodeList(newNumbers)
                .stream()
                .map(Hash::new)
                .toList();

        log.info("Generated {} hashes for batch", hashes.size());
        hashService.saveHashesBatch(hashes);
    }
}
