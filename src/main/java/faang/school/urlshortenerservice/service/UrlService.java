package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.url.HashNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional(readOnly = true)
    public Url getUrlByHash(String hash) {
        return urlRepository.findById(hash)
                .orElseThrow(() -> {
                    String errorMsg = String.format("Hash %s not found", hash);
                    log.error(errorMsg);
                    return new HashNotFoundException(errorMsg);
                });
    }
}
