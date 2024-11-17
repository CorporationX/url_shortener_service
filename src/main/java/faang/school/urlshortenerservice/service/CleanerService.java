package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanerService {

    @Value("${cleaner.deletion-period-days}")
    private final int deletionPeriodDays;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void clean() {
        LocalDateTime timestamp = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(deletionPeriodDays);
        List<Url> urls = urlRepository.deleteExpiredUrl(timestamp);
        List<Hash> hashes = urls.stream()
            .map(url -> Hash.builder().hash(url.getHash()).build())
            .toList();
        hashRepository.saveAll(hashes);

        List<String> hashStrings = hashes.stream()
            .map(Hash::getHash)
                .toList();
        hashStrings.forEach(this::clearCache);

        log.info("{} hashes have been released", hashStrings.size());
    }

    @CacheEvict("hash")
    public void clearCache(String hash) {}
}
