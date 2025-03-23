package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public List<Hash> getHashes(long count) {
        List<Hash> hashes = hashRepository.getHashBatch(count);

        if (hashes.size() < count) {
            hashGenerator.generateBatch();
            hashes = hashRepository.getHashBatch(count);
        }

        return hashes;
    }

    @Transactional
    @Async("hashGeneratorThreadPool")
    public CompletableFuture<List<Hash>> getHashesAsync(long count) {
        return CompletableFuture.completedFuture(getHashes(count));
    }

    public void deleteHash(Hash hash) {
        hashRepository.delete(hash);
    }
}
