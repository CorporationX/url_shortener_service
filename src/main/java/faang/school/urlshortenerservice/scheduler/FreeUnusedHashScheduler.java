package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FreeUnusedHashScheduler {
    private final UrlService urlService;

    //todo add executor
    @Scheduled() // todo add cron into yaml
    public void FreeUnusedHash() {
        urlService.FreeUnusedHash();
    }

}
