package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash-generator.batch-size}")
    private final int batchSize;

    @PostConstruct
    @Transactional
    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        List<String> stringHashes = getStringHashes();
        List<Hash> hashes = stringHashes.stream()
            .map(string -> Hash.builder().hash(string).build())
            .toList();
        hashRepository.saveAll(hashes);

        log.info("{} hashes added to the table", hashes.size());
    }

    public List<String> getStringHashes() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        return base62Encoder.encode(numbers);
    }
}
