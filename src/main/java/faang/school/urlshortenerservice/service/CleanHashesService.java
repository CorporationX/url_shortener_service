package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CleanHashesService {
    private final UrlService urlService;
    private final HashService hashService;

    @Transactional
    public void cleanHashes() {
        List<String> cleanedHashes = urlService.cleanHashes();
        hashService.saveAll(cleanedHashes);
    }
}
