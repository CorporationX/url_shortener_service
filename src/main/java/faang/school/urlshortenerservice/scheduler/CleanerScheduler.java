package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Async("threadPoolScheduler")
    @Scheduled(cron = "${scheduler.clean-old-urls.cron}")
    @Transactional
    public void clearOldUrls() {
        List<String> hashes = urlRepository.clearOldUrls();
        hashRepository.save(hashes);
    }

}
