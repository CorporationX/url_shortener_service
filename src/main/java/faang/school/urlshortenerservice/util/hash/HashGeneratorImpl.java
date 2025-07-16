package faang.school.urlshortenerservice.util.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final TaskExecutor hashGeneratorPool;

    @Value("${hash.generated_count}")
    private int generatedCount;

    @Async("hashGeneratorPool")
    @Override
    public void generateBatch() {
        log.info("Hashes generation started.");

        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(generatedCount);
        List<String> hashes = encoder.encodeBatch(uniqueNumbers);
        hashRepository.saveBatch(hashes);

        log.info("Hashes generated. From {} to {}", uniqueNumbers.get(0), uniqueNumbers.get(uniqueNumbers.size() - 1));
    }
}
