package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCashRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlCashRepository urlCashRepository;
    private final HashRepository hashRepository;

    public String getOriginUrl(String hash) {
        return urlCashRepository.getValue(hash)
                .orElse(hashRepository.findByHash(hash)
                        .orElseThrow(() -> new EntityNotFoundException("long url for: " + hash + " - does not exist"))
                        .getHash());
    }
}
