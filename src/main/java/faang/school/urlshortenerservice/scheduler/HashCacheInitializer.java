package faang.school.urlshortenerservice.scheduler;


import faang.school.urlshortenerservice.utils.HashCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashCacheInitializer {

    private final HashCache hashCache;

    @PostConstruct
    public void postConstructInit() {
        hashCache.initHashCache();
    }

    @Scheduled(cron = "#{@environment.getProperty('scheduling.hash-cache.cron')}")
    public void scheduledInit() {
        hashCache.initHashCache();
    }
}
