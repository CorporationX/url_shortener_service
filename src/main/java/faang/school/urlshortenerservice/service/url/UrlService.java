package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.ResponseDto;
import faang.school.urlshortenerservice.model.Url;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlService {

    List<Url> getAndDeleteOldUrls(LocalDateTime olderThan);

    ResponseDto createShortUrl(String originalUrl, HttpServletRequest request);

    String getUrlByHash(String hash);
}
