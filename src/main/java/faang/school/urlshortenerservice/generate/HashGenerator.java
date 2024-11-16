package faang.school.urlshortenerservice.generate;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final Executor asyncExecutor;

    @Value("${hash.range}")
    private int maxRange;

    @Value("${hash.numberOfParts}")
    private int numberOfParts;

    @Value("${hash.threshold}")
    private int ParallelProcessingThreshold;

    /* @Async(value = "asyncExecutor")*/
    public List<Hash> generateBatch(List<Long> numbers) {

        List<Hash> hashes;
        if (maxRange < ParallelProcessingThreshold) {
            hashes = getHashes(numbers);
        } else {
            hashes = getHashes(numbers, numberOfParts);
        }
        return hashes;
    }

    private List<Hash> getHashes(List<Long> numbers, int numberOfParts) {
        List<CompletableFuture<List<Hash>>> futures = IntStream.range(0, numberOfParts)
                .mapToObj(i -> CompletableFuture.supplyAsync(
                                () -> getHashesForRange(numbers, i, numberOfParts),
                                asyncExecutor)
                        .handle((result, ex) -> {
                            if (ex != null) {
                                throw new DataValidationException("Error receiving hashes " + ex.getMessage());
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
