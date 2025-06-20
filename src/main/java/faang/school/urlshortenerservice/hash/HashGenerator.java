package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final ExecutorService hashGeneratorThreadPool;

    @Value("${hash.generator.batch.size}")
    private int batchSize;

    @Async("hashGeneratorThreadPool")
    @Transactional
    public void generateBatch() {
        List<Long> availableNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = encoder.encode(availableNumbers);
        hashRepository.saveAllHashes(hashes);
        log.info("New batch of hashes has been saved");
    }
}
