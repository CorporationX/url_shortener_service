package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Async("generateHashExecutor")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(HashRepository.batchSize);
        List<String> hashes = encoder.encode(numbers);
        hashRepository.saveHashes(hashes);
    }
}