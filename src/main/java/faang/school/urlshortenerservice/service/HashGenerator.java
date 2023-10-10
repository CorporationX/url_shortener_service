package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashJpaRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashJpaRepository hashJpaRepository;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${uniqueNumbers}")
    private int uniqueNumber;

    @Async("batchExecutor")
    public void generateBatch() {
        Set<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumber);
        List<String> encode = base62Encoder.encode(uniqueNumbers);
        List<Hash> hashes = new ArrayList<>();
        for (String string : encode) {
            hashes.add(new Hash(string));
        }
        hashJpaRepository.saveBatch(hashes);
    }
}
