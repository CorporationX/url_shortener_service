package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.batch_size.generate}")
    private int generateBatchSize;

    @Async("hashGeneratorTaskExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(generateBatchSize);
        List<String> hashes = encoder.encode(uniqueNumbers);
        hashRepository.save(hashes);
    }
}
