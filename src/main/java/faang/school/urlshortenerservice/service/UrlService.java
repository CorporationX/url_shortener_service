package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlResponseDto;

public interface UrlService {

	UrlResponseDto createShortUrl(String longUrl);

	String getOriginalUrl(String hash);
}