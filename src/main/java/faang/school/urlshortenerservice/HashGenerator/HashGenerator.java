package faang.school.urlshortenerservice.HashGenerator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class HashGenerator {

    @Value("${app.hash.hash-count}")
    private final int hashCount;

    private final Base62Encoder base62Encoder;
    private final HashRepository repository;

    @Transactional
    @Async("hashGeneratorThreadPool")
    public void generateBatch() {
        try {
            List<Long> nums = repository.getUniqueNumbers(hashCount);
            List<String> hashes = base62Encoder.encode(nums);
            repository.saveHashes(hashes);
        } catch (Exception e) {
            log.error("Ошибка при генерации батча", e);
            throw e;
        }
    }
}
