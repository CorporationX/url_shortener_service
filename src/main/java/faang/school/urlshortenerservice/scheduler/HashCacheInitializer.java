package faang.school.urlshortenerservice.scheduler;


import faang.school.urlshortenerservice.utils.HashCache;
import faang.school.urlshortenerservice.utils.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashCacheInitializer {

    private final HashCache hashCache;
    private final HashGenerator hashGenerator;

    @Value("${hashGenerator.batchSize}")
    private final int batchSize;

    @Scheduled(cron = "${scheduling.hash-cache.cron}")
    public void scheduledInit() {
        hashGenerator.generateHash(batchSize);
        hashCache.fillCache();
    }
}
