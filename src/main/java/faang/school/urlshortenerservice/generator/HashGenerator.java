package faang.school.urlshortenerservice.generator;

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
    private final Base62Encoder base62Encoder;
    @Value("${hash.sequence-amount}")
    private  long sequenceAmount;

    @Async("fixedThreadPool")
    public void generateBatch(){
        try {
            List<Long> sequencesForHash = hashRepository.getUniqueNumbers(sequenceAmount);
            List<String> encodedHash = base62Encoder.encode(sequencesForHash);
            hashRepository.save(encodedHash);
        } catch (Exception e) {
            log.error("error during generating hashcodes: ", e);
            throw new RuntimeException(e);
        }
    }
}
