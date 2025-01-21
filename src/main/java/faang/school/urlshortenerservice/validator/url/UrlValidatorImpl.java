package faang.school.urlshortenerservice.validator.url;

import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlValidatorImpl implements UrlValidator {

    private final UrlRepository urlRepository;

    @Override
    public Url findByUrl(String url) {
        return urlRepository.findByUrl(url)
                .orElseGet(() -> {
                    log.warn("URL not found: {}", url);
                    return null;
                });
    }
}
