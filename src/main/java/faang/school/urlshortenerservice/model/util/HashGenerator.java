package faang.school.urlshortenerservice.model.util;

import faang.school.urlshortenerservice.repository.HashRepository;
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
    private final Base62Encoder base62Encoder;
    @Value("${hash.sequence-amount}")
    private int sequenceAmount;

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