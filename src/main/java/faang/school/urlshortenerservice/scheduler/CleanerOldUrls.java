package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerOldUrls {

    @Value("${hash.cleaner.year}")
    private int year;

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${hash.cleaner.cron}")
    @Transactional
    public void removeOldUrls() {
        List<String> oldHashes = urlRepository.removeOldLinks(LocalDateTime.now().minusYears(year));
        hashRepository.saveAll(oldHashes.stream()
                .map(Hash::new)
                .toList());
    }
}
