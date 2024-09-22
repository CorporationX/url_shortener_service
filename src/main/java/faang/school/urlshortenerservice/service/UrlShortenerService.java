package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void removingExpiredHashes(LocalDateTime dateExpired) {
        List<String> hashes = urlRepository.removeExpiredHash(dateExpired);
        if (!hashes.isEmpty()) {
            List<Hash> ListHash = hashes.stream()
                    .map(Hash::new)
                    .toList();
            hashRepository.saveAll(ListHash);
        }
    }
}
