package faang.school.urlshortenerservice.util.hash_generator;

import faang.school.urlshortenerservice.properties.short_url.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashProperties hashProperties;
    private final AsyncHashGenerator asyncHashGenerator;

    public List<String> getHashes(long count) {
        List<String> hashes = hashRepository.getHashBatch(count);
        if (hashes.size() < count) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(count - hashes.size()));
        }
        return hashes;
    }

    public void generateBatch() {
        long newHashesCount = hashProperties.getDbCreateMaxCount();
        log.info("Generating new {} hashes for urls...", newHashesCount);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(newHashesCount);
        List<CompletableFuture<Void>> creatingHashesFutures = getCreatingHashesFutures(uniqueNumbers);
        CompletableFuture.allOf(creatingHashesFutures.toArray(CompletableFuture[]::new)).join();
        log.info("Finished generating new {} hashes for urls!", newHashesCount);
    }

    private List<CompletableFuture<Void>> getCreatingHashesFutures(List<Long> uniqueNumbers) {
        return ListUtils.partition(uniqueNumbers, hashProperties.getDbCreateBatchSize()).stream()
                .map(asyncHashGenerator::generateAndSaveHashBatch)
                .toList();
    }
}
