package faang.school.urlshortenerservice.model.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    @Value("${hash.sequence-amount}")
    private int sequenceAmount;

    @Async("fixedThreadPool")
    public void generateBatch() {
        try {
            List<Long> sequencesForHash = hashRepository.getUniqueNumbers(sequenceAmount);
            log.info("sequence numbers to be hashed: {}", sequencesForHash);
            List<String> encodedHashes = encoder.encode(sequencesForHash);
            hashRepository.save(encodedHashes);
        } catch (Exception e) {
            log.error("error during generating hashcodes: ", e);
            throw new RuntimeException(e);
        }
    }
}
