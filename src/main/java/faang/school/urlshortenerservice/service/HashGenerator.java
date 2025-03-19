package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.batch.size}")
    private int batchSize;

    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Async("threadPoolTaskExecutor")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        encoder.encode(numbers).thenAccept(hashRepository::save);
    }
}
