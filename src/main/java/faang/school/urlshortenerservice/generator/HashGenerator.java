package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${spring.hash-generator.batch-count}")
    private static int batchAmount;

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Async("generateBatchThreadPool")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchAmount);
        List<String> hashes = encoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }
}
