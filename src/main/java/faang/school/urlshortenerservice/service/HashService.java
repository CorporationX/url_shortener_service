package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class HashService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${application.scheduler.period}")
    private int period;
    private final LocalDateTime dateTime = LocalDateTime.now().minusDays(period);

    @Transactional
    public void clean() {
        log.info("Clean hash urlRepository {}", LocalDateTime.now());
        List<Hash> updatingHashes = urlRepository.deleteOldUrl(dateTime).stream()
                .map(url -> new Hash(url.getHash()))
                .toList();

        hashRepository.saveAll(updatingHashes);
    }

}