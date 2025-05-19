package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.enity.FreeHash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final HashProperties hashProperties;
    private final HashGenerator hashGenerator;
    private final ExecutorService generateHashPool;


    @Transactional
    public List<String> getHashes() {
        List<String> hashes = hashRepository.findAndDelete(hashProperties.getGet().getMax());

        if (hashes.size() < hashProperties.getGet().getMin()) {
            CompletableFuture<Void> generateFuture =
                    CompletableFuture.supplyAsync(hashGenerator::generate, generateHashPool)
                            .thenAccept(this::saveAll);

            if (hashes.isEmpty()) {
                generateFuture.join();
                hashes = hashRepository.findAndDelete(hashProperties.getGet().getMax());
            }
        }
        return hashes;
    }

    @Async("saveHashesPool")
    public void saveAll(List<FreeHash> hashes) {
        hashRepository.saveAll(hashes);
    }
}
