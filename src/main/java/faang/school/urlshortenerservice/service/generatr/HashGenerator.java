package faang.school.urlshortenerservice.service.generatr;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${hash-generator.unique-numbers.batch-size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveHashes(hashes);
    }
}
