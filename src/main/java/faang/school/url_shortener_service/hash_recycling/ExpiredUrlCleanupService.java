package faang.school.url_shortener_service.hash_recycling;

import faang.school.url_shortener_service.entity.Hash;
import faang.school.url_shortener_service.entity.Url;
import faang.school.url_shortener_service.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpiredUrlCleanupService {
    private final UrlRepository urlRepository;
    private final RecycledHashHandler recycledHashHandler;
    @Value("${url.cleaner.list-partitions-size}")
    private int listPartitionsSize;
    @Value("${url.cleaner.timeout-for-recycling-minutes}")
    private int timeoutForRecycling;

    public void removeOldUrls() {
        long start = System.currentTimeMillis();
        List<Url> urls = urlRepository.deleteOldUrls();
        if (!urls.isEmpty()) {
            log.info("Found {} expired URLs. Starting recycling", urls.size());
            List<Hash> hashes = urls.stream()
                    .map(url -> new Hash(url.getHash()))
                    .toList();
            List<CompletableFuture<Void>> tasks = ListUtils.partition(hashes, listPartitionsSize)
                    .stream()
                    .map(recycledHashHandler::saveRecycledHashes)
                    .toList();
            try {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                        .get(timeoutForRecycling, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Some async hash recycling tasks timed out", e);
            }
            long duration = System.currentTimeMillis() - start;
            log.info("ExpiredUrlCleanupService finished, {} URL recycled in {} ms", urls.size(), duration);
        } else {
            log.info("Url cleaner ran - no old URLs found.");
        }
    }
}