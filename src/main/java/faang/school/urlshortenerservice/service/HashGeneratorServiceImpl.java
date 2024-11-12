package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashGeneratorServiceImpl implements HashGeneratorService {

    HashRepository hashRepository;
    Base62Encoder encoder;

    @Value("${spring.jpa.amount-hash}")
    private int amountHash;

    @Override
    @Async
    public CompletableFuture<List<Hash>> generateBatch() {
        try {
            List<Hash> hashes = encoder.encode(hashRepository.getUniqueNumbers(amountHash));
            return CompletableFuture.completedFuture(hashRepository.saveAll(hashes));
        } catch (Exception e) {
            CompletableFuture<List<Hash>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}
