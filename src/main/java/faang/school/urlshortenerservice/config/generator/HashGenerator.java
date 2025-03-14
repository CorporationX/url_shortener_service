package faang.school.urlshortenerservice.config.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${hash_repo.unique_max_size}")
    private int UNIQUE_MAX_SIZE;

    private final HashRepository repository;
    private final Base62Encoder encoder;

    @Transactional
    @Async("hashGeneratorThreadPool")
    public void generateBatch() {
        List<Long> uniqueNumbers = repository.getUniqueNumbers(UNIQUE_MAX_SIZE);
        List<String> hashes = encoder.encode(uniqueNumbers);
        repository.save(hashes);
    }
}
