package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${app.count_of_generated_hashes:10000}")
    private int countOfGeneratedHashes;

    @Transactional
    @Async("hashGeneratorThreadPool")
    public CompletableFuture<List<Hash>> generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(countOfGeneratedHashes);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers);
        return CompletableFuture.completedFuture(hashRepository.saveAll(hashes));
    }
}