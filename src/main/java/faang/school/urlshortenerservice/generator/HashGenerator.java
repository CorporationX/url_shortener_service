package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${generator.batch.size:10000}")
    private int batchSize;

    @Async("hashAsyncExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = encoder.encode(range)
                .stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
    }
}
