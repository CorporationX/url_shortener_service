package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepositoryJpa;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepositoryJpa hashRepositoryJpa;

    @Scheduled(cron = "${scheduled.cron.removeUrls}")
    @Transactional
    public void deleteOlderUrls() {
        List<String> hashes = urlRepository.deleteUrlsAndSaveHashes();
        List<Hash> listHashes = hashes.stream().map(Hash::new).toList();
        hashRepositoryJpa.saveAll(listHashes);
    }
}
