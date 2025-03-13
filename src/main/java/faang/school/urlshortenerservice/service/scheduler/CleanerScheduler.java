package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashService hashService;

    @Value("${hash.generate.amount:10000}")
    private int batchSize;

    @Scheduled(cron = "${cleaner.cron}")
    public void cleanOldUrls() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(1);
        log.info("CleanerScheduler: Starting cleaning of URLs older than {}", threshold);
        List<String> freedHashes = urlRepository.deleteOldUrls(threshold);

        if (freedHashes.isEmpty()) {
            log.info("CleanerScheduler: No old URLs found for cleaning.");
            return;
        }

        List<List<String>> batches = partitionList(freedHashes, batchSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (List<String> batch : batches) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                    hashService.saveHashes(batch)
            );
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("CleanerScheduler: Reinserted {} freed hashes into the hash table.", freedHashes.size());
    }

    private List<List<String>> partitionList(List<String> list, int size) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
}