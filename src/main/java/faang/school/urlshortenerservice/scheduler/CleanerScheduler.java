package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${scheduler.clear.cron}")
    public void deleteOldUrls() {
        List<Url> urls = urlRepository.findOldUrls();

        if (!urls.isEmpty()) {
            urlRepository.deleteAll(urls);
        }
    }
}