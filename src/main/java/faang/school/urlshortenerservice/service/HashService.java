package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
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

    @Transactional
    public List<Long> getUniqueNumbers(int count) {
        return hashRepository.getUniqueNumbers(count);
    }

    @Transactional
    public List<Hash> saveAll(List<Hash> hashes) {
        return hashRepository.saveAll(hashes);
    }

    @Transactional
    @Async("hashCacheThreadPool")
    public CompletableFuture<List<String>> getHashBatch(int count) {
        return CompletableFuture.completedFuture(hashRepository.getHashBatch(count));
    }

    @Transactional
    public Long count() {
        return hashRepository.count();
    }
}