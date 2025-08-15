package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.short_url.CreateShortUrlDto;

public interface ShortUrlService {
    String create(CreateShortUrlDto dto);
    String find(String code);
}
