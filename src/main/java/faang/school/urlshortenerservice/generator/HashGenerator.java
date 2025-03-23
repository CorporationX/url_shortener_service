package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashBatchRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("hashGeneratorImpl")
@RequiredArgsConstructor
public class HashGenerator {

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;
    private final HashBatchRepository hashBatchRepository;

    @Value("${url-shortener.hash.count-hash}")
    private long countUniqueHashes;

    @Transactional
    @Async("schedulerThreadPool")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(countUniqueHashes);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers);

        hashBatchRepository.saveHashByBatch(hashes);
    }
}
