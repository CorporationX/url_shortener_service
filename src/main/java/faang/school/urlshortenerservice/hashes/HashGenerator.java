package faang.school.urlshortenerservice.hashes;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${hash_repo.unique_max_size}")
    private int uniqueMaxSize;

    @Value("${hash.partition_size}")
    private int partitionSize;

    private final HashRepository repository;
    private final Base62Encoder encoder;

    @Autowired
    @Qualifier("hashGeneratorThreadPool")
    private ExecutorService pool;


    @Transactional
    @Async("hashGeneratorThreadPool")
    public CompletableFuture<Void> generateBatch() {
        List<Long> uniqueNumbers = repository.getUniqueNumbers(uniqueMaxSize);
        List<CompletableFuture<List<String>>> futureList = ListUtils
                .partition(uniqueNumbers, partitionSize)
                .stream()
                .map((values) -> CompletableFuture.supplyAsync(() -> encoder.encode(values), pool))
                .toList();
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        log.info("Parallel hash generation has been completed");
        List<String> hashes = futureList.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
        repository.save(hashes);
        log.info("The generated hashes are saved to the database");
        return CompletableFuture.completedFuture(null);
    }
}