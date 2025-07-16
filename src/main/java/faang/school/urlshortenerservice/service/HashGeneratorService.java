package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@RequiredArgsConstructor
public class HashGeneratorService {
    @Value("${hash-generation.hash-batch")
    private final int hashBatchAmount;
    @Value("${hash-generation.get-numbers")
    private final int uniqueNumbersAmount;

    private final HashRepository hashRepo;
    private final Base62Encoder encoder;

    @Async("Executor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepo.getUniqueNumbers(uniqueNumbersAmount);
        List<Hash> hashes = encoder.encode(uniqueNumbers).map(Hash::new).toList();
        hashRepo.saveAll(hashes);
    }
}
