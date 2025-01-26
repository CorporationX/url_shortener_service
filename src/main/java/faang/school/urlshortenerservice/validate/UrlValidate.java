package faang.school.urlshortenerservice.validate;

import faang.school.urlshortenerservice.entity.UrlAssociation;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UrlValidate {
    private final UrlRepository urlRepository;

    public UrlAssociation findExistingUrl(String originalUrl) {
        return urlRepository.findByUrl(originalUrl);
    }
}
