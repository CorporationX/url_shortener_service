package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder  base62Encoder;

    @Value("${hash.generator.batch.size}")
    private int batchSize;

    @Async("hashTaskExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);

        List<String> hashes = base62Encoder.encode(uniqueNumbers);

        hashRepository.saveBatch(hashes);
    }
}
