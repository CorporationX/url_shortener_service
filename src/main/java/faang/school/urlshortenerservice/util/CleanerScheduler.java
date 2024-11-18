package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.ShortLinkHashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerScheduler {

    private final ShortLinkHashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${hash.cache.cron.update}")
    @Transactional
    public void scheduledCleanUrls() {
        List<Hash> listHashes = urlRepository.deleteOneYearUrl();
        hashRepository.saveAll(listHashes);
    }
}
