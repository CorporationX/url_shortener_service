package faang.school.urlshortenerservice.scheduler.cleaner;

import faang.school.urlshortenerservice.dto.hash.HashDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.mapper.hash.HashMapper;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final HashMapper hashMapper;

    @Value("${scheduler.cleaning.url.expiration-interval}")
    private int expirationInterval;

    @Transactional
    @Scheduled(cron = "${scheduler.cleaning.url.cron}")
    public void cleanOldData() {
        log.info("Started job cleanOldData in " + CleanerScheduler.class);

        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(expirationInterval);
        List<String> stringHashList = urlRepository.deleteExpiredUrlsAndReturnHashes(cutoffDate);
        List<Hash> hashList = stringHashList.stream()
                .map(HashDto::new)
                .map(hashMapper::toEntity)
                .toList();

        hashRepository.saveAll(hashList);

        log.info("Finished job cleanOldData in " + CleanerScheduler.class);
    }
}