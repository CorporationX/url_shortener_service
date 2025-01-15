package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.model.Hash;

import faang.school.urlshortenerservice.repository.UniqueHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@AllArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final UniqueHashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${scheduled.cron_cleaning}")
    public void cleaner() {
        List<Hash> hashes = urlRepository.getAndDeleteAfterOneYear();
        hashRepository.saveAll(hashes);
    }

}
