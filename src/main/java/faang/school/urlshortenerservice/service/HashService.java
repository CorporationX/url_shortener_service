package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.event.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            try {
                hashGenerator.generateBatch();
                hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
            } catch (Exception e) {
                log.error("Ошибка при генерации или извлечении хэшей", e);
                throw new RuntimeException("Не удалось получить достаточное количество хэшей");
            }
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("hashThreadPoolTaskExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        try {
            List<String> hashes = getHashes(amount);
            return CompletableFuture.completedFuture(hashes);
        } catch (Exception e) {
            log.error("Ошибка при асинхронном извлечении хэшей", e);
            return CompletableFuture.failedFuture(new RuntimeException("Ошибка при асинхронном извлечении хэшей"));
        }
    }
}
