package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class UrlCleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${hash.scheduled:*/10 * * * * *}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting scheduled cleanup of old URLs");
        List<String> hashValues = urlRepository.deleteOldUrlsAndReturnHashes();
        hashRepository.saveAllBatch(mapToHashes(hashValues));
        log.info("Completed cleanup of old URLs, removed hashes: {}", hashValues.size());
    }

    private static List<Hash> mapToHashes(List<String> hashValues) {
        return hashValues.stream()
                .map(hashValue -> {
                    Hash hashEntity = new Hash();
                    hashEntity.setHash(hashValue);
                    return hashEntity;
                })
                .collect(Collectors.toList());
    }
}