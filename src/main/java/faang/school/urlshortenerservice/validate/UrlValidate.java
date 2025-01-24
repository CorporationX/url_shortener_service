package faang.school.urlshortenerservice.validate;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UrlValidate {
    private final UrlRepository urlRepository;

    public boolean presenceOfUrl(String originalUrl) {
        return urlRepository.existsByUrl(originalUrl);
    }
}
