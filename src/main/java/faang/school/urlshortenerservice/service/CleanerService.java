package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void clean() {
        LocalDateTime timestamp = LocalDateTime.now().toLocalDate().atStartOfDay().minusYears(1);
        List<Url> urls = urlRepository.deleteExpiredUrl(timestamp);
        List<Hash> hashes = urls.stream()
            .map(url -> Hash.builder().hash(url.getHash()).build())
            .toList();
        hashRepository.saveAll(hashes);
    }
}
