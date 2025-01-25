package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashJpaRepository;
import faang.school.urlshortenerservice.repository.UrlJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlCleanupService {
    private final HashJpaRepository hashJpaRepository;
    private final UrlJpaRepository urlJpaRepository;

    @Transactional
    public void cleanExpiresUrls(){
        List<String> releasedHashes = urlJpaRepository.deleteExpiredUrlsReturningHashes();
        hashJpaRepository.batchSave(releasedHashes);
    }

}
