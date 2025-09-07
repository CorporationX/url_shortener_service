package faang.school.urlshortenerservice.service.generator;


import faang.school.urlshortenerservice.common.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash.generator.range}")
    private int maxRange;

    @Override
    public List<String> generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getHashNumbers(maxRange);
        List<String> hashes = base62Encoder
                .encode(uniqueNumbers);
        hashRepository.saveAll(
                hashes.stream().map(Hash::new).toList()
        );
        return hashes;
    }

    @Override
    @Async("hashGeneratorExecutorService")
    public CompletableFuture<List<String>> generateBatchAsync() {
        List<String> hashes = generateBatch();
        return CompletableFuture.completedFuture(hashes);
    }
}