package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashService hashService;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash_generator.db_hashes_limit:50000}")
    private long dbHashesLimit;

    public void generate() {
        long currentSize = hashService.getHashesSize();
        if (currentSize < dbHashesLimit) {
            long need = dbHashesLimit - currentSize;
            log.info("HashGenerator: need to generate {} new hashes", need);

            List<Long> numbers = hashService.getUniqueNumbers(need);

            List<String> encoded = base62Encoder.encode(numbers);
            hashService.saveAllBatch(encoded);

            log.info("HashGenerator: saved {} new hashes in DB", encoded.size());
        } else {
            log.info("HashGenerator: currentSize = {}, no need to generate more", currentSize);
        }
    }

    public List<String> generateAndGet(int size) {
        List<Long> numbers = hashService.getUniqueNumbers(size);
        List<String> encoded = base62Encoder.encode(numbers);
        hashService.saveAllBatch(encoded);
        return encoded;
    }
}