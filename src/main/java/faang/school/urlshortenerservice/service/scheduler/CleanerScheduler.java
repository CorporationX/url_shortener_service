package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    @Value("${services.hash-service.created-before-months}")
    private int createdBeforeMonths;

    @Scheduled(cron = "${services.hash-service.cron-expression}")
    @Transactional
    public void performCronTask() {

        LocalDate now = LocalDate.now();
        log.info("Cron task executed at: " + now);
        List<Long> shouldBeDeleted = hashRepository.insertToHashDeletedUrls(
                now.minus(createdBeforeMonths, ChronoUnit.MONTHS), now);
        if (shouldBeDeleted == null || shouldBeDeleted.isEmpty()) {
            log.info("No URLs found for deletion.");
            return;
        }

        urlRepository.deleteAllByIdInBatch(shouldBeDeleted);
        log.info("Successfully deleted {} URLs.", shouldBeDeleted.size());
    }
}