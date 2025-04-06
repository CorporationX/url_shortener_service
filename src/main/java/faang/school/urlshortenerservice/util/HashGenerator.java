package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.sequence-size}")
    private Integer sequenceAmount;

    @Async("generateBatchFixedThreadPool")
    public void generateBatch() {
        try {
            List<Long> sequencesForHash = hashRepository.getUniqueNumbers(sequenceAmount);
            List<String> encodedSequences = encoder.encode(sequencesForHash);
            hashRepository.save(encodedSequences);
        } catch (Exception e) {
            log.error("Error while generating hash", e);
            throw new RuntimeException(e);
        }
    }
}