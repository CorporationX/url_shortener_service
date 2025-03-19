package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashCacheService {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Async("hashCacheThreadPool")
    public CompletableFuture<List<String>> getHashes() {
        List<String> hashes = hashRepository.getHashBatch();
        hashGenerator.generateBatch();
        return CompletableFuture.completedFuture(hashes);
    }
}
