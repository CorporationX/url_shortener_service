package faang.school.urlshortenerservice.generate;

import faang.school.urlshortenerservice.exception.ServiceException;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final ThreadPoolTaskExecutor asyncExecutor;

    @Value("${hash.range}")
    private long maxRange;

    @Value("${hash.numberOfParts}")
    private int numberOfParts;

    @Value("${hash.threshold}")
    private int threshold;

    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(maxRange);
        log.info("The list is unique and has been received.List size: {}", numbers.size());
        List<Hash> hashes;
        if (maxRange < threshold) {
            hashes = getHashes(numbers);
        } else {
            hashes = getHashes(numbers, numberOfParts);
        }
        hashRepository.saveAll(hashes);
        log.info("Saving a list of hashes in the database: {}", hashes.size());
    }

    private List<Hash> getHashes(List<Long> numbers, int numberOfParts) {
        List<CompletableFuture<List<Hash>>> futures = IntStream.range(0, numberOfParts)
                .mapToObj(i -> CompletableFuture.supplyAsync(
                                () -> getHashesForRange(numbers, i, numberOfParts),
                                asyncExecutor)
                        .handle((result, ex) -> {
                            if (ex != null) {
                                throw new ServiceException("Error receiving hashes " + ex.getMessage(), ex.getCause());
                            }
                            return result;
                        })
                ).toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Hash> getHashesForRange(List<Long> numbers, int partIndex, int totalParts) {
        int partitionSize = (numbers.size() + totalParts - 1) / totalParts;
        int start = partIndex * partitionSize;
        int end = Math.min(start + partitionSize, numbers.size());
        return getHashes(numbers.subList(start, end));
    }

    private List<Hash> getHashes(List<Long> numbers) {
        return base62Encoder.encode(numbers)
                .stream()
                .map(Hash::new)
                .toList();
    }
}
