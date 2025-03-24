package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class FreeHashGenerator {
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final Executor hashGeneratorExecutor;

    @Value("${shortener.hash-generator.chunk-size:5}")
    private int chunkSize;

    public FreeHashGenerator(@Qualifier("hashGenerator") Executor hashGeneratorExecutor) {
        this.hashGeneratorExecutor = hashGeneratorExecutor;
    }

    public List<FreeHash> generateHashes(List<Long> range) {

        List<List<Long>> partitions = ListUtils.partition(range, chunkSize);
        List<CompletableFuture<List<FreeHash>>> futures = partitions.stream()
                .map(chunk ->
                        CompletableFuture.supplyAsync(() ->
                                        chunk.stream()
                                                .map(this::applyBase62Encoding)
                                                .map(FreeHash::new)
                                                .toList(),
                                hashGeneratorExecutor))
                .toList();
        log.info("generating {} new hashes...", range.size());

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return builder.toString();
    }
}
