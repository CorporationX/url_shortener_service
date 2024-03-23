package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash-generator.unique-number.range}")
    private int uniqueNumberRange;

    @Transactional
    @Async("threadPoolForGenerateBatch")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumberRange);
        log.info("List of unique numbers in the amount of {} was taken from DB successfully", uniqueNumberRange);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(hashes);
        log.info("List of new hashes in the amount of {} was saved in DB successfully", uniqueNumberRange);
    }

}
