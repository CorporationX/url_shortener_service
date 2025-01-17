package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cleaner-start-cron-expression}")
    @Transactional
    public void deleteExpiredUrlsAndReturnHashesLaterThanOneYear() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Hash> hashes = hashRepository.deleteHashesLaterThan(oneYearAgo);
        hashRepository.saveAll(hashes);
    }
}
