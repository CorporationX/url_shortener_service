package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.crypto.BaseEncoder;
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

    private final BaseEncoder baseEncoder;
    private final HashRepository hashRepository;

    @Value("${hash.constants.batch-size}")
    private Long hashBatch;

    @Async("taskExecutor")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(hashBatch);
        List<String> hashes = baseEncoder.encode(numbers);
        hashRepository.saveBatch(hashes);
        log.info("Generated {} hashes", hashes.size());
    }
}
