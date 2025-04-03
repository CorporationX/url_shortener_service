package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashGeneratorService hashGeneratorService;

    @Async("hashGeneratorThreadPool")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        List<Hash> hashes = hashGeneratorService.getHashes(amount);
        List<String> hashesStr = hashes.stream()
                .map(Hash::getHash)
                .toList();
        return CompletableFuture.completedFuture(hashesStr);
    }
}
