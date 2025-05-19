package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;

    @Value("${url-shortener.hash-count}")
    private int count;

    @Transactional
    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        log.info("Starting generate {} hashes", count);

        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(count);
        List<String> uniqueStrings = base62Encoder.encode(uniqueNumbers);

        List<Hash> hashes = uniqueStrings.stream()
                .map(hash -> Hash.builder()
                        .hash(hash)
                        .build()
                ).toList();
        hashRepository.saveAll(hashes);

        log.info("Finished generate {} hashes", count);
    }
}
