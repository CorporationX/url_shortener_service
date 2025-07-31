package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final JdbcHashRepository jdbcHashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public void generateBatch(int batchSize) {
        var hashes = jdbcHashRepository.getUniqueNumbers(batchSize)
                .stream()
                .map(base62Encoder::encode)
                .toList();
        jdbcHashRepository.saveHashes(hashes);
    }
}
