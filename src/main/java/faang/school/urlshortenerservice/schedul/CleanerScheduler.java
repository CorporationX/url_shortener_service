package faang.school.urlshortenerservice.schedul;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    @Scheduled(cron = "${remove_old_url_cron}")
    public void removeOldUrl(){
        List<Hash> hashes = urlRepository.findAndRemoveAllOldEntity().stream()
                .map(Url::getHash)
                .peek(urlCacheRepository::deleteFormRedis)
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
    }
}
