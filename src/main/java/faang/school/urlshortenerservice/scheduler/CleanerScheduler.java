package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cron-to-clean-old-url}")
    @Transactional
    public void clean() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Url> urlList = urlRepository.findUrlsOlderThanOneYear(oneYearAgo);
        List<String> hashStringList = urlList.stream().map(Url::getHash).toList();
        urlRepository.deleteAll(urlList);
        List<Hash> hashList = hashStringList.stream().map(Hash::new).toList();
        hashRepository.saveAll(hashList);
    }

}
