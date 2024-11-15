package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;

import java.util.Optional;

public interface UrlService {

    UrlDto shortenUrl(UrlDto urlDto);

    Optional<Url> getUrl(String hash);

}
