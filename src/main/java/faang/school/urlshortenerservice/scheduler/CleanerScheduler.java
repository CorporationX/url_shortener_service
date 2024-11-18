package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.url.jpa.HashRepository;
import faang.school.urlshortenerservice.repository.url.jpa.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    @Value("${scheduler.cron.expression}")
    private String cronExpression;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final ThreadPoolTaskScheduler taskScheduler;


    @PostConstruct
    public void init() {
        taskScheduler.schedule(this::releaseHashes, new CronTrigger(cronExpression));
    }

    @Async
    @Transactional
    public void releaseHashes() {
        List<String> hashes = urlRepository.releaseHashes();
        hashRepository.saveAll(hashes, hashes.size());
    }

    @Async
    public void cleanCache() {

    }
}
