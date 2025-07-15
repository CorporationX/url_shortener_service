package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final Base62Encoder base62Encoder;
    private final HashService hashService;

    @Value("${hash-generator.batch-size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers =  hashService.getNextUniqueNumbers(batchSize);
        List<String> hashes =base62Encoder.generateHash(uniqueNumbers);
        hashService.saveHashes(hashes);
    }
}
