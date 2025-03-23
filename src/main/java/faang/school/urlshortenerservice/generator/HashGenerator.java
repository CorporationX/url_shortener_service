package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.number_hash_to_delete:1000}")
    @Setter
    private Integer numberHashToDelete;

    @Transactional
    public List<Hash> generateBatch(int batch) {
        List<Hash> hashes = new ArrayList<>();
        List<Long> numbers = hashRepository.getUniqueNumbers(batch);
        base62Encoder.encode(numbers).forEach(str -> hashes.add(new Hash(str)));

        return StreamSupport.stream(hashRepository.saveAll(hashes).spliterator(),false).toList();
    }

    @Async("cachedThreadPool")
    @Transactional
    public CompletableFuture<List<Hash>> generateBatchAsync(int batch) {
        return CompletableFuture.completedFuture(generateBatch(batch));
    }

}
