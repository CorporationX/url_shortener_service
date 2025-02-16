package faang.school.urlshortenerservice.modules.scheduler;

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
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${hash.scheduled}")
    public void startJob() {
        log.info("Start scheduled history cleaner.");
        try {
            hashRepository.cleanDataOlder1Year();
        } catch (Exception e){
            log.info("Error execution hashRepository.cleanDataOlder1Year");
        }
        log.info("End scheduled history cleaner.");
    }
}