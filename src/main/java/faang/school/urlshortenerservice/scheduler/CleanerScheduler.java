package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@RequiredArgsConstructor
public class CleanerScheduler {

    @Value("${hash.cleaner.cron}")
    private String cleanedCron;

    @Value("${hash.cleaner.interval}")
    private int interval;

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "#{cleanedCron}")
    @Transactional
    public void cleanOldAssociation() {
        List<Url> oldUrls = urlRepository.findAndDelete(interval);

        hashRepository.saveAll( oldUrls.stream().
                map(url -> new Hash(url.getHash())).
                toList());
    }
}
