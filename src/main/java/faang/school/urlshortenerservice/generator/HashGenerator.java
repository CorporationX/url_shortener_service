package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.exception.HashRetrievalException;
import faang.school.urlshortenerservice.repository.HashRepository;
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
    private final Base62Encoder encoder;

    @Value("${hash.range:1000}")
    private int maxRange;

    /**
     * Генерирует партию хэшей и сохраняет их в репозитории.
     *
     * @throws HashGenerationException Если произошла ошибка при генерации хэшей.
     */
    @Transactional
    public void generateBatch() {
        log.info("Начало генерации партии хэшей...");
        try {
            List<Long> range = hashRepository.getUniqueNumbers(maxRange);
            List<String> hashes = encoder.encode(range);
            List<Hash> list = hashes.stream()
                .map(Hash::new)
                .toList();
            hashRepository.saveAll(list);
            log.info("Успешно сгенерировано и сохранено {} хэшей.", list.size());
        } catch (Exception e) {
            log.error("Ошибка при генерации хэшей: {}", e.getMessage(), e);
            throw new HashGenerationException("Ошибка при генерации хэшей: " + e.getMessage(), e);
        }
    }

    /**
     * Получает партию хэшей из репозитория. Если хэшей недостаточно, генерирует новую партию.
     *
     * @param amount Количество хэшей, которые нужно получить.
     * @return Список хэшей.
     * @throws HashRetrievalException Если произошла ошибка при получении хэшей.
     */
    @Transactional
    public List<String> getHashBatch(long amount) {
        log.info("Запрос на получение {} хэшей...", amount);
        try {
            List<Hash> hashes = hashRepository.findAndDelete(amount);
            if (hashes.size() < amount) {
                log.info("Недостаточно хэшей в репозитории. Генерация новой партии...");
                generateBatch();
                hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
            }
            log.info("Успешно получено {} хэшей.", hashes.size());
            return hashes.stream().map(Hash::getHash).toList();
        } catch (Exception e) {
            log.error("Ошибка при получении хэшей: {}", e.getMessage(), e);
            throw new HashRetrievalException("Ошибка при получении хэшей: " + e.getMessage(), e);
        }
    }

    /**
     * Асинхронно получает партию хэшей.
     *
     * @param amount Количество хэшей, которые нужно получить.
     * @return CompletableFuture, содержащий список хэшей.
     * @throws HashRetrievalException Если произошла ошибка при получении хэшей.
     */
    @Transactional
    @Async("executorService")
    public CompletableFuture<List<String>> getHashBatchAsync(long amount) {
        log.info("Асинхронный запрос на получение {} хэшей...", amount);
        try {
            List<String> hashes = getHashBatch(amount);
            return CompletableFuture.completedFuture(hashes);
        } catch (Exception e) {
            log.error("Ошибка при асинхронном получении хэшей: {}", e.getMessage(), e);
            throw new HashRetrievalException("Ошибка при асинхронном получении хэшей: " + e.getMessage(), e);
        }
    }
}
