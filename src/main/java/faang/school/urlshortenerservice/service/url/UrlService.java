package faang.school.urlshortenerservice.service.url;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.urlshortenerservice.dto.UrlDto;
import jakarta.transaction.Transactional;

public interface UrlService {
    String getUrl(String hash) throws JsonProcessingException;

    UrlDto createShortUrl(String url) throws JsonProcessingException;

    @Transactional
    void cleanOldUrls();
}
