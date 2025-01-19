package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash.sequence-amount}")
    private long sequenceAmount;

    @Async("fixedThreadPool")
    public CompletableFuture<Void> generateBatch() {
        return CompletableFuture.runAsync(() -> {
            try {
                List<Long> sequencesForHash = hashRepository.getUniqueNumbers(sequenceAmount);
                List<String> encodedHash = base62Encoder.encode(sequencesForHash);
                hashRepository.saveBatch(encodedHash);
            } catch (Exception e) {
                log.error("error during generating hashcodes: ", e);
                throw new RuntimeException("Error during generating hashcodes: " + e.getMessage(), e);
            }
        });
    }
}
