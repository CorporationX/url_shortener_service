package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${url.expiration.years:1}")
    private int expirationYears;

    @Transactional
    public void cleanExpieredUrls(){
        LocalDateTime expirationDate = LocalDateTime.now().minusYears(expirationYears);
        List<String> deletedHashes = urlRepository.deleteExpiredUrls(expirationDate);
        hashRepository.save(deletedHashes);
    }
}
