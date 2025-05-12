package faang.school.urlshortenerservice.model.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    @Value("${hash.sequence-amount}")
    @NotNull(message = "Sequence amount must be specified")
    @Min(value = 1, message = "Sequence amount must be positive")
    private Integer sequenceAmount;

    @Async("fixedThreadPool")
    public void generateBatch() {
        try {
            List<Long> sequencesForHash = hashRepository.getUniqueNumbers(sequenceAmount);
            log.info("Sequence numbers to be hashed: {}", sequencesForHash);
            List<String> encodeHashes = encoder.encode(sequencesForHash);
            hashRepository.save(encodeHashes);
        } catch (Exception e) {
            log.error("Exception during generating hashcodes: ", e);
            throw new RuntimeException();
        }
    }
}