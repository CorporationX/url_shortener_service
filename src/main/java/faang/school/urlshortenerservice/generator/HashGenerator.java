package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueIdRepository;
import faang.school.urlshortenerservice.util.Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final UniqueIdRepository uniqueIdRepository;
    private final Encoder<Long, Hash> encoder;

    @Value("${server.hash.generate.batch.size}")
    private int generateBatchSize;

    @Value("${server.hash.generator.batch.chunk-size}")
    private int chunkSize;

    @Transactional
    @Async("customTaskExecutor1")
    public void generateBatch() {
        List<Long> uniqueNumbers = uniqueIdRepository.getUniqueNumbers(generateBatchSize);
//        List<Hash> hashes = processChunk(uniqueNumbers);
        List<Hash> hashes = encoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }

    @Async("customTaskExecutor2")
    public List<Hash> processChunk(List<Long> uniqueNumbers) {
        List<Hash> hashes = encoder.encode(uniqueNumbers);
        return hashes;
    }
}
