package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.url.CreateUrlDto;

public interface UrlService {

    void cleanExpiredHashes(int yearsAgoToDeleteHashes);

    String createShortUrl(CreateUrlDto createUrlDto);

    String getOriginalUrl(String hash);
}