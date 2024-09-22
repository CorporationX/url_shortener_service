package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.exception.HashGenerateException;
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

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;
    @Value("${spring.generator.batch-size}")
    private int batchSize;

    @Async
    public void generateBatch() {
        try {
            List<Long> batchNumbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> batchHashes = base62Encoder.batchEncoding(batchNumbers);
            hashRepository.batchSave(batchHashes);
        } catch (Exception e) {
            log.error("Hash generating failed: {}", e.getMessage());
            throw new HashGenerateException("Hash generating failed: " + e.getMessage());
        }
    }
}