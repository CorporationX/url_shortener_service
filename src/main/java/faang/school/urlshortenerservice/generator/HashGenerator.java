package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.properties.HashConfig;
import faang.school.urlshortenerservice.encoder.Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final HashConfig hashConfig;
    private final Encoder encoder;

    @Async("fixedThreadPool")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<List<Hash>> generateBatch() {
        log.debug("Start async generation of new hashes batch");
        List<Long> numbers = hashRepository.getUniqueNumbers(hashConfig.getBatchSize());
        List<Hash> hashes = encoder.encode(numbers).stream()
                .map(Hash::new)
                .toList();
        log.info("Hashes was successfully generated");
        return CompletableFuture.supplyAsync(() -> hashRepository.saveAll(hashes));
    }
}