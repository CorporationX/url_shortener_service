package faang.school.urlshortenerservice.cache.hash;


import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unbrokendome.base62.Base62;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;

    @Value("${hash.batch-size:10}")
    private int batchSize;

    @Transactional
    public void generateHashes() {
        List<Long> nextBatch = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = nextBatch.stream()
                .map(Base62::encode)
                .map(hash -> hash.replaceFirst("^0+(?!$)", ""))
                .collect(Collectors.toList());
        hashRepository.saveAll(hashes);
    }

    @Transactional
    @Async("hashGenExecutor")
    public CompletableFuture<Void> generateHashesAsync() {
        generateHashes();
        return CompletableFuture.completedFuture(null);
    }
}
