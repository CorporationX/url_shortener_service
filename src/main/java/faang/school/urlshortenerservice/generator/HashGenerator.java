package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.context.HashGeneratorConfig;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repo.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashGeneratorConfig config;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Void> generateBatch() {
        try {
            // Получаем n уникальных чисел из БД
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(config.getBatchSize());

            // Конвертируем числа в base62 хэши (используем новый метод encode(List<Long>))
            List<String> hashes = base62Encoder.encode(uniqueNumbers);

            // Сохраняем хэши в БД
            hashRepository.saveAllHashes(hashes);

            log.info("Generated and saved {} hashes", hashes.size());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error generating hash batch", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}