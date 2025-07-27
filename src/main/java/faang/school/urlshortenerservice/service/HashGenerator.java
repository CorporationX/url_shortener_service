package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashConfig hashConfig;
    private final Base62Encoder base62Encoder;

    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashConfig.getStorage().getSize());
        List<String> encodedBatch = base62Encoder.encodeBatch(uniqueNumbers, hashConfig.getLength());
        List<Hash> hashList = new java.util.ArrayList<>(encodedBatch.stream()
                .map(Hash::new)
                .toList());

        Collections.shuffle(hashList);
        hashRepository.saveAll(hashList);
        log.info("Hashes generated: {}", hashList.size());
    }
}
