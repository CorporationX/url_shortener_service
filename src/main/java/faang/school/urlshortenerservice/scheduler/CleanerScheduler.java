package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Transactional
    @Scheduled(cron = "${url.cleaner.schedule.cron}")
    public void cleanOldUrls() {
        List<String> hashes = urlRepository.removeOldUrls();
        hashRepository.saveAll(hashes.stream().map(Hash::new).toList());
    }
}
