package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.cache.hash.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FillInScheduler {

    private final HashGenerator hashGenerator;

    @Scheduled(cron = "${hash.schedule.fill-in.cron:0 0 * * * ?}")
    @SchedulerLock(name = "FillInScheduler_addNewFreeHashes",
            lockAtLeastFor = "PT2M", lockAtMostFor = "PT10M")
    public void addNewFreeHashes(){
        log.debug("Starting generating new free hashes...");
        hashGenerator.generateHashesAsync();
        log.info("New free hashes have been added to table hash");
    }
}
