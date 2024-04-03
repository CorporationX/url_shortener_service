package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashScheduler {
private  final HashCache hashCache;

    @Scheduled(cron = "${scheduler.get-hash.cron}")
    public void getHash() {
        hashCache.generateHashCache();
    }
}