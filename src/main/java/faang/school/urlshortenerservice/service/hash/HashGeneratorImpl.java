package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.sequence.UniqueNumberSequenceRepository;
import faang.school.urlshortenerservice.service.base62encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {

    @Value("${hash.generation.batch-size:50}")
    private static int BATCH_SIZE;
    @Value("${hash.generation.batch-partition:5}")
    private static int BATCH_PARTITION;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final UniqueNumberSequenceRepository uniqueNumberSequenceRepository;

    @Override
    @Transactional
    @Async("hashGenerationExecutor")
    public CompletableFuture<List<Hash>> generateBatch() {
        List<Long> numbers = uniqueNumberSequenceRepository.getUniqueNumbers(BATCH_SIZE);

        if(numbers.isEmpty()) {
            log.info("No numbers to generate hashes");
            throw new RuntimeException("No numbers to generate hashes");
        }
        List<Hash> hashes = ListUtils.partition(numbers, BATCH_PARTITION).stream()
                .map(nums -> CompletableFuture
                        .supplyAsync(() -> base62Encoder.encodeListNumbers(nums), Executors.newSingleThreadExecutor()))
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .map(Hash::new)
                .toList();
        log.info("Generated hashes: {}", hashes);
        return CompletableFuture.completedFuture(hashRepository.saveAll(hashes));
    }
}
