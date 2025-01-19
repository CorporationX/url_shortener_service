package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final int batchSize;

    public HashGenerator(
            HashRepository hashRepository,
            Base62Encoder base62Encoder,
            @Value("${hash-generating.batch-size}") int batchSize
    ) {
        this.hashRepository = hashRepository;
        this.base62Encoder = base62Encoder;
        this.batchSize = batchSize;
    }

    @Async("hashGeneratorThreadPool")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.saveAll(mapHashes(hashes));
    }

    private List<Hash> mapHashes(List<String> hashes) {
        return hashes.stream()
                .map(s -> Hash.builder()
                        .hash(s)
                        .build()
                )
                .toList();
    }

}
