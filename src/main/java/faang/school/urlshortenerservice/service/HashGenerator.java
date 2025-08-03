package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashProperties hashProperties;

    @Async("hashGeneratorExecutor")
    @Retryable(
            maxAttemptsExpression = "#{@retryConfig.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@retryConfig.delay}",
                    multiplierExpression = "#{@retryConfig.multiplier}"
            )
    )
    public void generateBatch() {
        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(
                    hashProperties.getBatchSize()
            );
            if (uniqueNumbers.isEmpty()) {
                log.warn("HashGenerator: получен пустой список уникальных чисел. Хэши не будут сгенерированы.");
                return;
            }

            List<String> hashes = Base62Encoder.encode(uniqueNumbers);

            hashRepository.save(hashes);
        } catch (Exception e) {
            log.error("Ошибка при генерации батча хэшей", e);
            throw e;
        }
    }
}
