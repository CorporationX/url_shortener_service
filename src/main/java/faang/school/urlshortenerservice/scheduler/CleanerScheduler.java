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

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${schedule.cron.clear-urls}")
    public void clearUrls() {
        List<Hash> hashes = urlRepository.deleteOlderThanYear().stream()
                .map(Hash::new)
                .toList();
        log.info("Удалили старые ссылки: {}, по расписанию.", hashes.size());
        hashRepository.save(hashes.stream()
                .map(Hash::getHash)
                .toList()
        );
    }

}