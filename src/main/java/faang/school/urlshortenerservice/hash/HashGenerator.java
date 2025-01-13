package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.asyncExecutor.AsyncExecutor;
import faang.school.urlshortenerservice.config.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${batchSize}")
    private long batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final AsyncExecutor asyncExecutor;

    public void generateBatch() {
        List<Long> forHashGenerate = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = base62Encoder.encode(forHashGenerate);
        hashRepository.saveAll(hashes);
    }

}
