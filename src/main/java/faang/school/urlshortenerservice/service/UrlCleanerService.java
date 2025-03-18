package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UrlCleanerService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${app.scheduler.url_cleaner.url_lifetime_days}")
    private int days;

    @Transactional
    public void removeExpiredUrlsAndResaveHashes() {
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(days);

        List<String> hashes = urlRepository.deleteOldUrlsAndReturnHashes(expirationDate);

        List<Hash> hashEntities = hashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashEntities);
    }
}