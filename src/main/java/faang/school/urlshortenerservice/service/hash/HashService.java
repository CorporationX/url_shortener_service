package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;
    private final HashMapper hashMapper;
    private final HashGenerator hashGenerator;

    @Transactional
    public void saveHashes(List<String> hashes) {
        hashRepository.saveAll(hashMapper.toEntity(hashes));
    }

    @Transactional
    public List<Long> getNextUniqueNumbers(int count) {
        return hashRepository.getNextUniqueNumbers(count);
    }

    @Transactional
    @Async("hashCacheFillerExecutor")
    public CompletableFuture<List<Hash>> getHashes(long amount) {
        List<Hash> hashes = hashRepository.getAndDeleteHashBatch(amount);
        if (hashes.size() < amount) {
            hashGenerator.generateBatch();
            hashes.addAll(hashRepository.getAndDeleteHashBatch(amount - hashes.size()));
        }
        return CompletableFuture.completedFuture(hashes);
    }
}
