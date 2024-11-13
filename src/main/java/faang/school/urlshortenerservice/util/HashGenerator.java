package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.postgres.hash.HashRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    @Value("${app.hashes-generation.batch-size:80}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashService hashService;

    @Transactional
    public void generateBatchOfHashes() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashService.saveAll(hashes);
    }
}
