package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlRetrieverService {
    private final UrlRepository urlRepository;

    @Cacheable(value = "urls", key = "#hash", unless = "#result == null || #result.isEmpty()")
    @Transactional(readOnly = true)
    public Optional<String> getLongUrl(String hash) {
        log.info("Cache miss for hash '{}'. Fetching from database.", hash);
        return urlRepository.findById(hash).map(Url::getUrl);
    }
}
