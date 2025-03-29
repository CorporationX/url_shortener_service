package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;

    @Value("${hash-generator.batch-size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    @Transactional()
    public void generateBatch() {
        log.info("Generating hash batch");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = Base62Encoder.encodeBatch(uniqueNumbers);
        List<String> hashStrings = hashes.stream()
            .map(Hash::getHash)
            .toList();

        String[] hashArray = hashStrings.toArray(new String[0]);
        hashRepository.saveBatch(hashArray);
    }
}
