package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashService hashService;
    @Value("${services.hash-service.created-before-months}")
    private int createdBeforeMonths;

    @Scheduled(cron = "${services.hash-service.cron-expression}")
    @Transactional
    public void performCronTask() {

        hashService.performCronTaskTransactional(createdBeforeMonths);
    }
}