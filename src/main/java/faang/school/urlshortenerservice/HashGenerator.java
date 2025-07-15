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
    private static final String BASE_62_CHARACTERS = "abcdefghijklnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final JdbcHashRepository jdbcHashRepository;

    @Transactional
    @Async("generatorExecutor")
    @Scheduled(cron = "${hash.schedule-cron}")
    public void generateBatch() {
        jdbcHashRepository.saveHashes(jdbcHashRepository.getUniqueNumbers(uniqueIdsPerBatch).stream()
                .map(this::applyBase62Encoding)
                .toList());
    }

    private String applyBase62Encoding(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return sb.reverse().toString();

    }

}
