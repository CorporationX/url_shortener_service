package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range:1000}")
    private int uniqueNumberRange;

    @Transactional
    @Async("asyncExecutor")
    public void generateHash() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumberRange);
        log.info("Получили список уникальных номеров кол-вом: {} из БД", uniqueNumberRange);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        log.info("Получили список хэшей {} из энкодера", uniqueNumbers);
        hashRepository.save(hashes);
        log.info("Список новых хэшей кол-вом {} сохранен в БД", uniqueNumberRange);
        CompletableFuture.completedFuture(null);
    }

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        log.info("Получили {} хэшей из БД", amount);
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Async("asyncExecutor")
    @Transactional
    public CompletableFuture<List<Hash>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

}