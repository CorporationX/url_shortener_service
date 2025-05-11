package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Компонент, отвечающий за генерацию и сохранение уникальных хэшей для коротких URL.
 * <p>
 * Использует {@link Base62Encoder} для преобразования уникальных чисел в строки, пригодные для использования в ссылках.
 * Регулярно проверяет текущее количество доступных хэшей и генерирует новые при необходимости.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash-generator.count-generate-hashes}")
    private int countGenerateHashes;

    @Value("${hash-generator.min-free-ratio-hashes}")
    private double minHashRatio;

    /**
     * Метод инициализации, запускается при старте приложения.
     * Проверяет текущее количество хэшей и при необходимости генерирует новые.
     */
    @PostConstruct
    public void init() {
        log.debug("Init hashes after application startup");
        checkAndGenerateHashes();
    }

    /**
     * Асинхронный метод проверки и генерации хэшей.
     * Используется {@link HashService} через {@link java.util.concurrent.ExecutorService}.
     */
    @Transactional
    public void checkAndGenerateHashesAsync() {
        log.debug("Start check and generate hashes");
        checkAndGenerateHashes();
    }

    private void checkAndGenerateHashes() {
        long hashesCount = hashRepository.getCountOfHashes();
        if (needToGenerateHashes(hashesCount)) {
            generateHashes(countGenerateHashes);
        }
    }

    private void generateHashes(int count) {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(count);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveHashes(hashes);

        log.debug("Hashes generated successfully");
    }

    private boolean needToGenerateHashes(double currentHashesCount) {
        return currentHashesCount / countGenerateHashes < minHashRatio;
    }
}
