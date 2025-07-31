package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledHashGenerator {
    private final HashGenerator hashGenerator;

    @Scheduled(cron = "${hash-generation.scheduler_cron}")
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastFor = "PT1M", lockAtMostFor = "PT5M")
    public void generateHash() {
        hashGenerator.generateHash();
    }
}
