package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import liquibase.database.jvm.HsqlConnection;
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
import java.util.stream.StreamSupport;


@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public List<Hash> generateBatch(int batch) {
        List<Hash> hashes = new ArrayList<>();
        List<Long> numbers = hashRepository.getUniqueNumbers(batch);
        log.info("Received {} unique numbers from hash repository", numbers.size());
        base62Encoder.encode(numbers).forEach(str -> hashes.add(new Hash(str)));
        log.info("{} numbers encode to base 62", numbers.size());
        List<Hash> list = StreamSupport.stream(hashRepository.saveAll(hashes).spliterator(), false).toList();
        log.info("Hashes {} saved to hash repository", list.size());
        return list;
    }

    @Async("cachedThreadPool")
    @Transactional
    public CompletableFuture<List<Hash>> generateBatchAsync(int batch) {
        return CompletableFuture.completedFuture(generateBatch(batch));
    }

}
