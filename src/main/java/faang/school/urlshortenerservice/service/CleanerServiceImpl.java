package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerServiceImpl implements CleanerService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${url.expiration:PT1Y}")
    private Duration expirationPeriod;

    @Transactional
    public void cleanExpiredUrls(){
        LocalDateTime expirationDate = LocalDateTime.now().minus(expirationPeriod);
        List<String> deletedHashes = urlRepository.deleteExpiredUrls(expirationDate);
        hashRepository.save(deletedHashes);
    }
}
