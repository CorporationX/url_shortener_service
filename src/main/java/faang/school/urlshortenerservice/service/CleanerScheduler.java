package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
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

    @Scheduled(cron = "${cleaner-scheduler.cron}")
    @Transactional
    public void cleanOldUrls() {
        List<Url> oldUrls = urlRepository.findOldUrls();

        List<Hash> hashes = oldUrls.stream()
                .map(Url::getHash)
                .map(Hash::new)
                .toList();

        hashRepository.saveHashesList(hashes);
    }
}
