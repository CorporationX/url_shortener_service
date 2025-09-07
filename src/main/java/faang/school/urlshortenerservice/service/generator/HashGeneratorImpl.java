package faang.school.urlshortenerservice.service.generator;


import faang.school.urlshortenerservice.common.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash.generator.range}")
    private int maxRange;

    @Override
    public void generateBatch() {
        List<Long> hashNumbers = hashRepository.getHashNumbers(maxRange);
        List<Hash> hashes = base62Encoder
                .encode(hashNumbers).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    @Override
    @Async("hashGeneratorExecutorService")
    public void generateBatchAsync() {
        generateBatch();
    }
}