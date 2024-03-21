package faang.school.urlshortenerservice.util;

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
    private final Base62Encoder base62Encoder;
    @Value("${hash_generator.batch_size}")
    private int batchSize;

    @Async("threadPoolTaskExecutor")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.save(hashes);
    }
}