package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {
    public Url getOriginalUrl(String hash);

    public UrlDto convertLongUrl(String longUrl);
}
