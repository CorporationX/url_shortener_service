package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.Hash.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void cleanOldUrls() {
        LocalDateTime yearAgo = LocalDateTime.now().minusYears(1);

        List<String> oldHashes = urlRepository.deleteOldUrls(yearAgo);

        if (!oldHashes.isEmpty()) {
            hashRepository.save(oldHashes); 
        }

    }
}
