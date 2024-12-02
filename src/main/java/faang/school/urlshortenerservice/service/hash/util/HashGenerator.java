package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.util.encode.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder encoder;

    @Value("${app.hash_generator.db_hashes_limit}")
    private long dbHashesLimit;

    public void generate() {
        Long dbHashesSize = hashService.getHashesSize();

        if (dbHashesSize < dbHashesLimit) {
            List<Long> numbers = hashService.getUniqueNumbers(dbHashesLimit - dbHashesSize);
            List<String> hashes = encoder.encode(numbers);

            List<Hash> hashesEntity = hashes.stream()
                    .map(Hash::new)
                    .toList();

            hashService.saveAllBatch(hashesEntity);
        }
    }

    public List<String> generateAndGet(int size) {
        List<Long> numbers = hashService.getUniqueNumbers(size);

        return encoder.encode(numbers);
    }
}
