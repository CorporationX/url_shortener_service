package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${shortener.url-cron}")
    public void deleteExpiredUrls() {
        log.info("Start to release expired hashes");
        List<Url> urls = urlRepository.deleteExpiredUrls();

        List<Hash> hashes = urls.stream()
                .map(url -> Hash.builder().hash(url.getHash()).build())
                .toList();

        hashRepository.saveAll(hashes);
        log.info("Released {} of hashes", hashes.size());
    }
}
