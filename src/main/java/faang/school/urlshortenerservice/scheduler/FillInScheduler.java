package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.cache.hash.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FillInScheduler {

    private final HashGenerator hashGenerator;

    @Value("${cleaner.cron.fillInTime:0 0 0 * * ?}")
    private String fillInTime;

    @Scheduled(cron = "${hash.schedule.fill-in.cron:0 0/5 * * * ?}")
    @SchedulerLock(name = "FillInScheduler_addNewFreeHashes",
            lockAtLeastFor = "PT2M", lockAtMostFor = "PT10M")
    public void addNewFreeHashes(){
        log.debug("Starting generating new free hashes...");
        hashGenerator.generateHashesAsync();
        log.info("New free hashes have been added to table hash");
    }
}
