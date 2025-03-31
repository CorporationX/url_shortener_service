package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaintenanceService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public void cleanUpOldUrls() {
        List<Url> oldUrls = urlRepository.findAllExpiredUrls(LocalDateTime.now());
        List<String> hashes = oldUrls.stream()
            .map(Url::getHash)
            .toList();

        urlCacheRepository.deleteAll(hashes);
        urlRepository.deleteAll(oldUrls);

        List<Hash> hashEntities = hashes.stream()
            .map(Hash::new)
            .toList();

        hashRepository.saveAll(hashEntities);
    }
}
