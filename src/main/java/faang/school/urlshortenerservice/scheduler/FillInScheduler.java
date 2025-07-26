package faang.school.urlshortenerservice.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

public class FillInScheduler {

    @Value("${cleaner.cron.fillInTime:0 0 0 * * ?}")
    private String fillInTime;

    @Scheduled(cron = "${hash.schedule.cron:0 0/5 * * * ?}")
}
