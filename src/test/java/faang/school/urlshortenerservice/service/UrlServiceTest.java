package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCash;
import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCash hashCash;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    private Url url;
    private UrlDtoRequest urlDtoRequest;
    private String hash;

    @BeforeEach
    void init() {
        hash = "Sd5";
        String urlString = "http://example.com";
        urlDtoRequest = UrlDtoRequest.builder()
                .url(urlString)
                .build();
        url = Url.builder()
                .url(urlString)
                .hash(hash)
                .build();
    }
}
