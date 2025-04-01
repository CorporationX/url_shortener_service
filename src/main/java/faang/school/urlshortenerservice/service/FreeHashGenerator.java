package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FreeHashGenerator {
    private static final String BASE_62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE_62_LENGTH = 62;
    private static final long ADVISORY_LOCK = 99;

    private final FreeHashRepository freeHashRepository;

    @Transactional
    public void refillDb(Long capacity) {
        boolean lockAcquired = freeHashRepository.tryAdvisoryLock(ADVISORY_LOCK);
        if (!lockAcquired) {
            log.info("Another instance is already generating hashes. Skipping...");
            return;
        }

        List<Long> numbers = freeHashRepository.generateBatch(capacity);

        log.info("generating {} new hashes...", capacity);

        List<FreeHash> hashes = numbers.stream()
                .map(this::applyBase62Encoding)
                .map(FreeHash::new)
                .toList();

        freeHashRepository.saveAll(hashes);
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_LENGTH)));
            number /= BASE_62_LENGTH;
        }
        return builder.toString();
    }
}
