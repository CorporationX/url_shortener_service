package faang.school.urlshortenerservice.HashGenerator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
//@RequiredArgsConstructor
public class HashGenerator {

    //@Value("${app.hash.hash-count}")
    private final int hashCount;

    private final Base62Encoder base62Encoder;
    private final HashRepository repository;

    @Autowired
    public HashGenerator(@Value("${app.hash.hash-count}") int hashCount,
                         Base62Encoder base62Encoder,
                         HashRepository repository) {
        this.hashCount = hashCount;
        this.base62Encoder = base62Encoder;
        this.repository = repository;
    }

    @Async("hashGeneratorThreadPool")
    public CompletableFuture<Void> generateBatch() {
        try {
            List<Long> nums = fetchUniqueNumbers(hashCount);
            List<String> hashes = base62Encoder.encode(nums);
            saveHashes(hashes);
            log.debug("Сгенерировано и сохранено {} хешей", hashes.size());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Ошибка при генерации батча", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        if (hashes.isEmpty()) {
            log.error("Нет хешей для сохранения. В списке: {}", hashes.size());
            throw new DataValidationException("В списке нет хешей");
        }
        repository.saveHashes(hashes);
    }

    @Transactional(readOnly = true)
    public List<Long> fetchUniqueNumbers(int count) {
        if (count <= 0) {
            throw new DataValidationException("Количество хешей должно быть положительным");
        }
        return repository.getUniqueNumbers(count);
    }
}
