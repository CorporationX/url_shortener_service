package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.async.AsyncProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final AsyncProperties asyncProperties;

    @Value("${hash.batch.size}")
    private int n;

    @Async("asyncExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);

        List<String> encodedHashes = base62Encoder.encode(uniqueNumbers);

        List<Hash> hashes = encodedHashes.stream().map(encodedHash -> Hash.builder()
                        .hash(encodedHash)
                        .build())
                .toList();

        hashRepository.saveHashes(hashes);
    }
}

