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

    @Value("${hashGenerator.batchSize:1000}")
    private int batchSize;

    @Async("hashExecutor")
    public void generateBatch() {
        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> encode = base62Encoder.encode(uniqueNumbers);
            hashRepository.save(encode);
        } catch (Exception e) {
            log.error("Exception during generating hashcodes: ", e);
            throw new RuntimeException();
        }
    }
}