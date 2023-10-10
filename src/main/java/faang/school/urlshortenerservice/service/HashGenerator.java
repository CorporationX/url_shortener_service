package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashJdbcRepository hashJpaRepository;
    private final Base62Encoder base62Encoder;
    @Value("${uniqueNumbers}")
    private long uniqueNumber;

    @Async("batchExecutor")
    public void generateBatch() {
        Set<Long> uniqueNumbers = hashJpaRepository.getUniqueNumbers(uniqueNumber);
        List<String> encode = base62Encoder.encode(uniqueNumbers);
        List<Hash> hashes = new ArrayList<>();
        for (String string : encode) {
            hashes.add(new Hash(string));
        }
        hashJpaRepository.saveBatch(hashes);
        log.info("Hashes was successfully generated {}", hashes);
    }
}
