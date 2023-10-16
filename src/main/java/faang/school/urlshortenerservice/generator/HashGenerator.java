package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash_generator.unique_numbers}")
    private int uniqueNumbers;

    @Async
    public List<Hash> generateBatch() {
        return getHashes();
    }

    public List<Hash> generateBatchNotAsync() {
        return getHashes();
    }

    private List<Hash> getHashes() {
        log.info("HashGenerator started");

        List<Long> numbers = hashRepository.getUniqueNumbers(uniqueNumbers);
        List<Hash> hashes = base62Encoder.encode(numbers);
        hashRepository.save(hashes);

        log.info("HashGenerator end");
        return hashes;
    }

}