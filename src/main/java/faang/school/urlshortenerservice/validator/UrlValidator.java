package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlValidator {

    private final UrlRepository urlRepository;

    public boolean validateUrlByAlreadyExists(String url) {
        return urlRepository.existsByUrl(url);
    }
}
