package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCleaner {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Value("${scheduler.interval-to-clean}")
    private Duration intervalToClean;

    @Transactional
    @Scheduled(cron = "${scheduler.hash-cleaner-schedule}")
    public void cleanExpiredHashes() {
        LocalDateTime cleaningTime = LocalDateTime.now().minus(intervalToClean);
        List<String> hashStrings = urlRepository.getExpiredHashes(cleaningTime);
        List<Hash> hashes = convertToHash(hashStrings);
        hashRepository.saveAll(hashes);
        log.info("{} hashes removed from short url database and moved to hash repository", hashes.size());
    }

    private List<Hash> convertToHash(List<String> hashes) {
        return hashes.stream()
                .map(Hash::new)
                .toList();
    }
}
