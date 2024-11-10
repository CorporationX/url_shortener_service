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
    LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void clean() {
        List<Url> urlList = urlRepository.findUrlsOlderThanOneYear(oneYearAgo);
        List<String> hashStringList = urlList.stream().map(Url::getHash).toList();
        urlRepository.deleteAll(urlList);
        List<Hash> hashList = hashStringList.stream().map(Hash::new).toList();
        hashRepository.saveAll(hashList);
    }

}
