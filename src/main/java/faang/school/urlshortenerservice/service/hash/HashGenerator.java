package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> generateHashBatch(List<Long> uniqueNumbers) {
        log.info("Generating hashes for {} unique numbers", uniqueNumbers.size());
        List<String> hashes = base62Encoder.generateHash(uniqueNumbers);
        return CompletableFuture.completedFuture(hashes);
    }
}
