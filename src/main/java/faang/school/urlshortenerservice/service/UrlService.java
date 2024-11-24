package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.request.UrlRequest;
import faang.school.urlshortenerservice.dto.response.UrlResponse;

import java.util.List;

public interface UrlService {

    List<String> deleteUnusedUrls();

    void updateUrls(List<String> hashes);

    UrlResponse shortenUrl(UrlRequest request);

    String getUrl(String hash);
}
