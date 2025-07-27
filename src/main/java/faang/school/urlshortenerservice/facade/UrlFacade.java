package faang.school.urlshortenerservice.facade;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class UrlFacade {

    private final UrlService urlService;

    @Value("${app.short-url}")
    private String hashUrlPrefix;

    public UrlResponse createShortUrl(UrlRequest urlRequest) {
        String hashUrl = urlService.createShortUrl(urlRequest);

        URI urlUri = UriComponentsBuilder
                .fromHttpUrl(hashUrlPrefix)
                .path(hashUrl)
                .build()
                .toUri();

        return UrlResponse.builder()
                .url(urlUri)
                .build();
    }

    public UrlResponse getShortUrl(String hash) {
        String url = urlService.getShortUrl(hash);

        URI uri = UriComponentsBuilder.fromUriString(url)
                .build()
                .toUri();

        return UrlResponse.builder()
                .url(uri)
                .build();
    }
}
