package faang.school.urlshortenerservice.service;

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
    private final Base62Encoder base62Encoder;
    @Value("${hash-generator.generate-amount:100}")
    private int amount;

    @Async(value = "hashGeneratorTaskExecutor")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(amount);
        List<Hash> hashes = base62Encoder.encode(numbers);
        hashRepository.saveAll(hashes);
    }
}
