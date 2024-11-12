package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.encoding.Base62Encoder;
import faang.school.urlshortenerservice.repository.postgres.hash.HashRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    @Value("${app.hash.batch-size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashService hashService;

    @Async("hashesGeneratorThreadPool")
    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashService.saveAll(hashes);
    }
}
