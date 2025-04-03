package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlCleaningService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void cleanAndSaveHashes() {
        List<String> hashes = urlRepository.deleteOldUrlsAndReturnHashes();
        if (!hashes.isEmpty()) {
            hashRepository.saveAllHashes(hashes);
        }
    }
}
