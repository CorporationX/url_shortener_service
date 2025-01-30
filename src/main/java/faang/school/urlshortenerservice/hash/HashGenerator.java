package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashService hashService;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range:1000}")
    private int range;

    @Async("hashGeneratorPool")
    @Transactional
    public void generateBatch() {
        log.info("Generating new batch of hashes");

        List<Long> uniqueNumbers = hashService.getUniqueNumbers(range);
        List<String> encodedNumbers = base62Encoder.encode(uniqueNumbers);

        List<Hash> hashes = encodedNumbers.stream()
                .map(Hash::new)
                .toList();

        hashService.saveHashes(hashes);
        log.info("{} hashes success generated", range);
    }



}
