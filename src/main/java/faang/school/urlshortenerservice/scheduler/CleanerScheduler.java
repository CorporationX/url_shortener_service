package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    @Value("${url-cleanup.retention-period}")
    private String retentionPeriod;

    @Scheduled(cron = "${cron.expressions.releasing-сashe}")
    @Transactional
    public void releasingCashe() {
        List<String> hashes = urlRepository.deleteOldLinks(retentionPeriod);
        hashRepository.saveBatch(hashes);
    }
}
