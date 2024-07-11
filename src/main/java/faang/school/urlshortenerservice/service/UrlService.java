package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlCreatedRequest;
import faang.school.urlshortenerservice.dto.UrlCreatedResponse;
import faang.school.urlshortenerservice.model.Url;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlService {

    UrlCreatedResponse createUrl(UrlCreatedRequest urlCreatedRequest);

    String getUrl(String hash);

    List<Url> findUrlsCreatedBefore(LocalDateTime dateTime);

    void deleteAll(List<Url> urls);
}
