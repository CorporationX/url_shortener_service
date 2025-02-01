package faang.school.urlshortenerservice.modules.scheduler;

import faang.school.urlshortenerservice.config.scheduler.SchedulerConfig;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryCleaner {
    private final SchedulerConfig schedulerConfig;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "#{@schedulerConfig.cronHistoryTime}")
    public void startJob() {
        log.info("Start scheduled history cleaner.");
        hashRepository.getHistoryCleaner();
        log.info("End scheduled history cleaner.");
    }
}