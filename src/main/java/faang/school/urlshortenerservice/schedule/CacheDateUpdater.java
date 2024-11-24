package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.listener.TtlExpiredHashStorage;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheDateUpdater {

    private final TtlExpiredHashStorage expiredHashStorage;
    private final UrlService urlService;

    @Scheduled(cron = "${url.schedule.cron}")
    public void updateCacheDate() {
        List<String> hashes = expiredHashStorage.getHashesToUpdate();
        urlService.updateUrls(hashes);
    }
}
