package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
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

    @Value("${hash.batch-size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("customThreadPool")
    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = numbers.stream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
    }
}
