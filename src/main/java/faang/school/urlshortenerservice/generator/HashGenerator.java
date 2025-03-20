package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashService hashService;
    private final Base62Encoder base62Encoder;

    @Value("${url-shortener.count-hash}")
    private long countUniqueHashes;

    @Transactional
    @Async("threadPool")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashService.getUniqueNumbers(countUniqueHashes);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers);

        hashService.saveHashByBatch(hashes);
    }
}
