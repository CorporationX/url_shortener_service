package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlCleanerService {
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void cleanOldUrls(LocalDateTime dateTime) {
        List<Hash> hashes = urlRepository.deleteOldUrlsAndReturnHashes(dateTime).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }
}
