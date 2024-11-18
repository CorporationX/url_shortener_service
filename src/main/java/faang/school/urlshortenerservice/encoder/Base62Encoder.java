package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.hash.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class Base62Encoder {

    private static final String BASE_62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Async
    public CompletableFuture<List<Hash>> encode(List<Long> numbers, int threadsAmount) {
        log.debug("Encoding batch of {} amount", numbers.size());
        List<List<Long>> partitions = partitionList(numbers, threadsAmount);
        List<CompletableFuture<List<Hash>>> futures = partitions.stream()
                .map(this::encodePartition)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(f -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .toList()
                );
    }

    private List<List<Long>> partitionList(List<Long> initialList, int numberOfPartitions) {
        int partitionSize = (int) Math.ceil((double) initialList.size() / numberOfPartitions);
        return IntStream.range(0, numberOfPartitions)
                .mapToObj(i -> initialList.subList(i * partitionSize,
                        Math.min((i + 1) * partitionSize, initialList.size())))
                .toList();
    }

    public CompletableFuture<List<Hash>> encodePartition(List<Long> numbers) {
        log.debug("Encoding partition of {} amount", numbers.size());
        return CompletableFuture.completedFuture(
                numbers.stream()
                        .map(this::encodeNumber)
                        .map(Hash::new)
                        .toList()
        );
    }

    private String encodeNumber(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE_62.charAt((int) (number % BASE_62.length())));
            number /= BASE_62.length();
        }
        return sb.toString();
    }
}
