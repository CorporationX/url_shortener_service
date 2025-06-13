package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashProperties hashProperties;

    @Async("hashExecutor")
    public void generateBatchAsync() {
        try {
            log.info("Асинхронный запуск генерации хэшей");
            generateBatch();
        } catch (Exception e) {
            log.error("Ошибка при асинхронной генерации хэшей: {}", e.getMessage(), e);
        }
    }

    @Transactional
    private void generateBatch() {
        int batchSize = hashProperties.getBatchSize();
        log.info("Запускается генерация хэшей. Размер пакета: {}", batchSize);
        log.debug("Поток: {}", Thread.currentThread().getName());

        List<Long> numbers = fetchUniqueNumbers(batchSize);
        validateNotEmpty(numbers, "Список уникальных номеров пуст.");

        List<Hash> hashes = Base62Encoder.encode(numbers);
        validateNotEmpty(hashes, "Список хэшей оказался пустым после кодирования.");

        hashRepository.saveAll(hashes);
        log.info("Успешно сохранено {} хэшей в БД.", hashes.size());
    }

    @Transactional(readOnly = true)
    private List<Long> fetchUniqueNumbers(int count) {
        return hashRepository.getUniqueNumbers(count);
    }

    private <T> void validateNotEmpty(List<T> list, String errorMessage) {
        if (CollectionUtils.isEmpty(list)) {
            log.warn(errorMessage);
            throw new HashGenerationException(errorMessage);
        }
    }
}