package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueIdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final UniqueIdRepository uniqueIdRepository;
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${app.generation-batch-size:100}")
    private int generationBatch;

    @Transactional
    @Async("taskExecutor")
    public void generateBatch() {
        List<Long> seeds = uniqueIdRepository.getNextRange(generationBatch);
        List<String> hashes = seeds.stream()
                .map(encoder::encode)
                .toList();
        hashRepository.saveHashes(hashes);
    }

    @Transactional
    public List<String> getHashes(int amount) {
        return hashRepository.getHashes(amount);
    }
}
