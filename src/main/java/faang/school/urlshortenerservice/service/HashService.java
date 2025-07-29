package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dao.HashDao;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class HashService {
    private final HashDao hashDao;
    private final Base62Encoder base62Encoder;
    private final Executor hashExecutor;
    @Value("${hashes.generate.batch-size:1000}")
    private int hashesGenerateSize;
    @Value("${hashes.generate.chunk-size:250}")
    private int hashesChunkSize;

    public void generateHashes() {
        List<Long> longs = hashDao.generateSequenceValues(hashesGenerateSize);
        List<List<Long>> chunks = getChunks(longs, hashesChunkSize);

        List<CompletableFuture<Void>> savedHashesFutures = chunks.stream()
                .map(chunk -> CompletableFuture.runAsync(() -> {
                    List<String> hashes = base62Encoder.encode(chunk);
                    hashDao.insertHashes(hashes);
                    log.info("Processed chunk of size {}", chunk.size());
                }, hashExecutor))
                .toList();

        savedHashesFutures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                log.error("Error while generating hashes", e);
            }
        });
    }

    @Async("hashExecutor")
    public CompletableFuture<List<String>> getHashes(@Min(1) int batchSize) {
        try {
            List<String> hashes = hashDao.deleteAndReturnHashes(batchSize);
            checkStorageFilling();

            return CompletableFuture.completedFuture(hashes);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public List<List<Long>> getChunks(List<Long> list, int chunkSize) {
        List<List<Long>> chunks = new ArrayList<>();
        int listSize = list.size();

        for (int i = 0; i < listSize; i += chunkSize) {
            int end = Math.min(listSize, i + chunkSize);
            List<Long> chunk = list.subList(i, end);
            chunks.add(new ArrayList<>(chunk));
        }

        return chunks;
    }

    private void checkStorageFilling() {
        int hashesNum = hashDao.checkStorageFilling();
        if ((hashesGenerateSize * 100) / hashesNum < 50) {
            generateHashes();
        }
    }
}