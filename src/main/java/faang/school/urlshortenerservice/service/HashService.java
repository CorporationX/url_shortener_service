package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;

    @Value("${spring.cache.limit}")
    private int limit;

    @Async
    @Transactional
    public CompletableFuture<List<String>> findAndDelete() {
        return CompletableFuture.supplyAsync(() -> hashRepository.findAndDelete(limit));
    }

}
