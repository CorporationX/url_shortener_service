package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.url.jpa.HashRepository;
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

    @Value("${generator.numbers-batch-size:1000}")
    private int uniqueNumbersBatchSize;

    @Async("asyncExecutor")
    public void generateBatch() {
        List<String> hashes = base62Encoder.encode(hashRepository.getUniqueNumbers(uniqueNumbersBatchSize));
        hashRepository.saveAll(hashes);
    }
}
