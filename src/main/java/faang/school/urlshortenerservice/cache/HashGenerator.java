package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash-generator.batch-size}")
    private final int batchSize;

    @Async("hashGeneratorExecutor")
    @PostConstruct
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> stringHashes = base62Encoder.encode(numbers);
        List<Hash> hashes = stringHashes.stream()
            .map(string -> Hash.builder().hash(string).build())
            .toList();
        hashRepository.saveAll(hashes);
    }
}
