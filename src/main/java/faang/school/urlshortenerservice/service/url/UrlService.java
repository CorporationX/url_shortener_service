package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.CreateUrlRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlService {
    String create(CreateUrlRequest dto);
    String getOriginalUrl(String hash);
    List<String> cleanOldUrls(LocalDateTime cutoff);
}
