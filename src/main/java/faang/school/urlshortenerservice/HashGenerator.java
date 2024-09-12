package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Hash;
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
    @Value("${spring.hash.batch_size:1000}")
    private int batchSize;

    @Async
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = encoder.encode(numbers).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }
}
