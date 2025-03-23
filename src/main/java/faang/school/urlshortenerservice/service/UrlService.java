package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;

public interface UrlService {

  String makeShortUrl(UrlDto urlDto);

  String getLongUrl(String hash);
}
