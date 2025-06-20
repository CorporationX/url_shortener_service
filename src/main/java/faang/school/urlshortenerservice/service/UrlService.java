package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;

public interface UrlService {

    ResponseUrlDto shorten(RequestUrlDto requestUrlDto);

    ResponseUrlDto getOriginalUrl(String hash);
}