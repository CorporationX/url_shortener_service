package faang.school.urlshortenerservice.service.url_shortener;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash_cashe.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    public UrlDto shortenUrl(UrlDto longUrlDto) {
        //Logic
        return new UrlDto();
    }
}
