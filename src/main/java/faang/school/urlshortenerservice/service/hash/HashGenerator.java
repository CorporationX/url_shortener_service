package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Encoder encoder;

    @Value("${hash-generator.batch-size}")
    private long batchSize;

    @Autowired
    public HashGenerator(
            HashRepository hashRepository,
            @Qualifier("base62Encoder") Encoder encoder
    ) {
        this.hashRepository = hashRepository;
        this.encoder = encoder;
    }

    @Async("threadPool")
    public CompletableFuture<Void> generateBatch() {
        log.info("Generating {} hashes", batchSize);
        List<Integer> uniqueNumbers = hashRepository.getNUniqueNumbers(batchSize);
        List<String> hashes = encoder.encodeNumbers(uniqueNumbers);
        hashRepository.save(hashes);
        return CompletableFuture.completedFuture(null);
    }
}
