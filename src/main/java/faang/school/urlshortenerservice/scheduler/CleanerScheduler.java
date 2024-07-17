package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${custom.cleaner.cron}")
    @Transactional
    public void cleanOldUrls(){
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<String> expiredHashes = urlRepository.deleteExpiredUrlsAndReturnHashes(oneYearAgo);
        List<Hash> hashesToSave = expiredHashes.stream()
                .map(Hash::new)
                .collect(Collectors.toList());
        hashRepository.saveAll(hashesToSave);
    }
}
