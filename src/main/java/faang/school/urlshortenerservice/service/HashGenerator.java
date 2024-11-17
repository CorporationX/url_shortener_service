package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utility.base62.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;

    @Value(value = "${generator.batch-size}")
    private long batchSize;

    @Async("HashGeneratorPool")
    public void generateBatch() {
        List<Long> nums = hashRepository.getUniqueNumbers(batchSize);
        List<String> stringHashes = Base62Encoder.encode(nums);
        List<Hash> hashes = stringHashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
    }
}
