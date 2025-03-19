package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        encodeAndSaveAsync(numbers);
    }

    @Async("threadPoolTaskExecutor")
    public void encodeAndSaveAsync(List<Long> numbers) {
        encoder.encode(numbers).thenAccept(hashRepository::save);
    }
}
