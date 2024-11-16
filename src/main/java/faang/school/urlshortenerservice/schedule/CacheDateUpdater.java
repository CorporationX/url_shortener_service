package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.listener.RedisKeyExpiredEventListener;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheDateUpdater {

    private final RedisKeyExpiredEventListener listener;
    private final UrlService urlService;

    @Scheduled(cron = "${url.schedule.cron}")
    public void updateCacheDate() {
        List<String> hashes = listener.getHashesToUpdate();
        urlService.updateUrls(hashes);
    }
}
