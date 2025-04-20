package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.dto.UrlDto;
import reactor.core.publisher.Mono;

public interface UrlService {

    public Mono<UrlDto> shortenUrl(String url);

    public Mono<String> getOriginalUrl(String hash);
}