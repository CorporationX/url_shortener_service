package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashService hashService;

    @Transactional
    public void findAndDelete() {
        List<String> hashes = urlRepository.findAndDelete();
        if (!hashes.isEmpty()) {
            hashService.saveHashes(hashes);
        }
    }
}
