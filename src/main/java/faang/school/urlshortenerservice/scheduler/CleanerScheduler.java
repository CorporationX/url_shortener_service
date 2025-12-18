package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${cleanup.cron}")
    @Transactional
    public void cleanupOld() {
        List<UrlEntity> expired = urlRepository.findExpired();
        hashRepository.returnHashes(
                expired.stream().map(UrlEntity::getHash).collect(Collectors.toList())
        );
        urlRepository.deleteAll(expired);
    }
}