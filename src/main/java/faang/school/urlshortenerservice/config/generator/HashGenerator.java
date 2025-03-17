package faang.school.urlshortenerservice.config.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${hash_repo.unique_max_size}")
    private int UNIQUE_MAX_SIZE;

    @Value("${hash.partition_size}")
    private int PARTITION_SIZE;

    private final HashRepository repository;
    private final Base62Encoder encoder;
    @Autowired
    @Qualifier("hashGeneratorThreadPool")
    private ExecutorService pool;

    @Transactional
    @Async("hashGeneratorThreadPool")
    public CompletableFuture<Void> generateBatch() {
        List<Long> uniqueNumbers = repository.getUniqueNumbers(UNIQUE_MAX_SIZE);
        List<CompletableFuture<List<String>>> futureList = ListUtils
                .partition(uniqueNumbers, PARTITION_SIZE)
                .stream()
                .map((values) -> CompletableFuture.supplyAsync(() -> encoder.encode(values)))
                .toList();
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        List<String> hashes = futureList.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
        repository.save(hashes);
        return CompletableFuture.completedFuture(null);


    }
}
