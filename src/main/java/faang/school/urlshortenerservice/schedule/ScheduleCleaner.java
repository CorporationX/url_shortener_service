package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.service.CleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleCleaner {
    private final CleanerService cleanerService;

    @Scheduled(cron = "${cleaner.schedule-cron}")
    public void schedule(){
        cleanerService.cleanStateUrl();
    }
}
