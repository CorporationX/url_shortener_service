package faang.school.urlshortenerservice.scheduled;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpiredUrlsCleaner {

    private final UrlRepository urlRepository;
    private final HashService hashService;

    @Scheduled(cron = "${shortener.schedule.url-cron}")
    public void deleteExpiredUrls() {
        log.info("Start to release hashes of expired urls");
        List<Url> urls = urlRepository.deleteExpiredUrls();
        List<Hash> hashes = urls.stream()
                .map(url -> new Hash(url.getHash()))
                .toList();
        hashService.saveHashes(hashes);
        log.info("Released {} of hashes", hashes.size());
    }
}
