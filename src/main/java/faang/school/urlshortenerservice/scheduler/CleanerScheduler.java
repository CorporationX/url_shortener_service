package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@EnableScheduling
public class CleanerScheduler {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private HashRepository hashRepository;

    @Scheduled(cron = "${hash.cleaner.cron}")
    @Transactional
    public void cleanUpOldUrls() {
        List<String> oldHashes = urlRepository.deleteOldUrlsAndReturnHashes();
        hashRepository.saveBatch(oldHashes);
    }
}

