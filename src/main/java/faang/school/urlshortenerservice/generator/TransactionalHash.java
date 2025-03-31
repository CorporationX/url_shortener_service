package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionalHash {
    private final HashRepository hashRepository;

    @Value("${hash-generator.batch-size}")
    private int batchSize;

    @Transactional
    public void generateAndSaveHashes() {
        log.info("Generating hashes in transaction");
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = Base62Encoder.encodeBatch(uniqueNumbers);

        hashRepository.saveAll(hashes);
    }
}
