package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${cleaner_scheduler.period_clear_years}")
    private int periodClear;

    @Scheduled(cron = "${scheduled.cleaner_scheduler}")
    @Transactional
    public void clearOldUrlAndHash() {
        List<String> oldHash = urlRepository.removeOldHash(periodClear);
        List<Hash> result = oldHash.stream()
                .map(hash -> Hash.builder()
                        .hash(hash)
                        .build())
                .toList();
        hashRepository.saveAll(result);
    }
}
