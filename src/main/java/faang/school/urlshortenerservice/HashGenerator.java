package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class HashGenerator {

    @Value("${uniqueIdsPerBatch}")
    private int uniqueIdsPerBatch;
    private final JdbcHashRepository jdbcHashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    @Async("generatorExecutor")
    @Scheduled(cron = "${hash.schedule-cron}")
    public void generateBatch() {
        jdbcHashRepository.saveHashes(jdbcHashRepository.getUniqueNumbers(uniqueIdsPerBatch).stream()
                .map(base62Encoder::encode)
                .toList());
    }
}
