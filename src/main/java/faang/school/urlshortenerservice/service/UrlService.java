package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${hash.hash-cleanup.period}")
    private Duration hashCleanupPeriod;

    @Transactional
    public void deleteOldUrl() {
        LocalDateTime fromDate = LocalDateTime.now().minus(hashCleanupPeriod);
        List<String> hashes = urlRepository.removeOldUrlAndGetFreeHashes(fromDate);
        if(!hashes.isEmpty()) {
            hashRepository.saveHashBatch(hashes);
            log.info("Очистка завершена: {} хешей теперь свободны.", hashes.size());
        }
    }
}
