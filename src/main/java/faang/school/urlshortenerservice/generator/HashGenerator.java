package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.batchSize}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("asyncExecutorToHashGenerator")
    public void generateBatch() throws ExecutionException, InterruptedException {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = (base62Encoder.encode(uniqueNumbers).get().stream().map(Hash::new).toList());
        hashRepository.saveAll(hashes);
    }
}
