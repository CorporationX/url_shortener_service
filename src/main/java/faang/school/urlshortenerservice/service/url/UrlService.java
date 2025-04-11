package faang.school.urlshortenerservice.service.url;

import reactor.core.publisher.Mono;

public interface UrlService {

    public Mono<String> shortenUrl(String url);

    public Mono<String> getOriginalUrl(String hash);
}