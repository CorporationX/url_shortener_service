package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UrlService {
    public Url getOriginalUrl(String hash);

    public UrlDto convertLongUrl(Url longUrl);

    public List<String> cleanOldUrls();
}
