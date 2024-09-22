package faang.school.urlshortenerservice.manage;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlSchedulerManager {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void removingExpiredHashes(LocalDateTime dateExpired) {
        List<String> hashes = urlRepository.removeExpiredHash(dateExpired);
        if (!hashes.isEmpty()) {
            hashRepository.save(hashes);
        }
    }
}
