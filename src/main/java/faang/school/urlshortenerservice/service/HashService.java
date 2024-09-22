package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.UrlHash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.UrlHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashGenerator hashGenerator;
    private final UrlHashRepository urlHashRepository;
    private final int maxGeneratedHashes;

    private void generateUrlHashes(int amount) {
        List<UrlHash> urlHashes = hashGenerator.generateHashes(amount)
                .map(UrlHash::new)
                .toList();

        urlHashRepository.saveAll(urlHashes);
    }

    @Transactional
    public void generateUrlHashes() {
        generateUrlHashes(maxGeneratedHashes);
    }

    @Transactional
    public void fillHashesIfNecessary() {
        int actualHashesCount = (int) urlHashRepository.count();
        if (actualHashesCount < maxGeneratedHashes) {
            int lacking = maxGeneratedHashes - actualHashesCount;
            generateUrlHashes(lacking);
        }
    }

    @Async("hashGeneratorThreadPool")
    @Transactional
    public CompletableFuture<List<String>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<UrlHash> hashes = urlHashRepository.popAll(amount);

        if (hashes.size() < amount) {
            generateUrlHashes(); // TODO mb invoke something like generateUrlHashes((hashes.size() - amount) * 1.2)
        }

        int lackingAmount = amount - hashes.size();
        hashes.addAll(urlHashRepository.popAll(lackingAmount));

        return hashes.stream()
                .map(UrlHash::getHash)
                .toList();
    }
}
