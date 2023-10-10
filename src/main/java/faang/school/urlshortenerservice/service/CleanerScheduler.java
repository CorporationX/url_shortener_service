package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${application.schedule.period:P1Y}")
    private String period; // ISO-8601 format

    @Scheduled(cron = "${application.schedule.cleaner:0 0 0 * * *}")
    @Transactional
    public void clean() {
        log.info("Staring cleaning old urls");
        LocalDateTime targetDate = getTargetDate();
        List<Url> oldUrls = urlRepository.getAndDeleteAllByCreatedAtBefore(targetDate);
        List<Hash> hashes = oldUrls.stream()
                .map((url) -> new Hash(url.getHash()))
                .toList();
        hashRepository.saveAll(hashes);
    }

    private LocalDateTime getTargetDate() {
        Duration duration = Duration.parse(period);
        return LocalDateTime.now()
                .minus(duration);
    }
}
