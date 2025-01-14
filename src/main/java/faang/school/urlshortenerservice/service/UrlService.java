package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    public UrlDto createShortUrl(UrlDto urlDto) {
        return new UrlDto("shortener/123456");
    }

    public String getLongUrl(String hash) {
        return "https://www.google.com/";
    }
}
