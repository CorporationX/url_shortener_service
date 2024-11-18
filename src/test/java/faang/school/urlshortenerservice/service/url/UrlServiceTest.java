package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.dto.url.RequestUrlBody;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.mapper.url.UrlMapper;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static final String URL = "URL";
    private static final String HASH = "HASH";

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private Url url;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "urlShortPrefix", "ANY");

        url = Url.builder()
                .build();
    }

    @Test
    @DisplayName("When method called then return List values")
    void whenMethodCalledThenNoThrownException() {
        urlService.findAndReturnExpiredUrls(anyInt());

        verify(urlRepository)
                .deleteAndReturnExpiredUrls(anyInt());
    }

    @Test
    @DisplayName("When url already exists then return")
    void whenUrlAlreadyInRepositoryThenNotCreatedNewUrl() {
        RequestUrlBody requestUrlBody = RequestUrlBody.builder()
                .url(URL)
                .build();

        when(urlRepository.findByUrlIgnoreCase(anyString()))
                .thenReturn(Optional.of(url));

        urlService.convertUrlToShort(requestUrlBody);

        verify(urlRepository)
                .findByUrlIgnoreCase(anyString());
        verify(urlMapper)
                .toResponseBody(any(Url.class), anyString());
        verify(hashCache, times(0))
                .getHash();
        verify(urlRepository, times(0))
                .save(any(Url.class));
        verify(urlCacheRepository, times(0))
                .save(any(Url.class));
    }

    @Test
    @DisplayName("When url not exists then create new one")
    void whenUrlNotExistsInRepositoryThenNotCreatedNewUrl() {
        RequestUrlBody requestUrlBody = RequestUrlBody.builder()
                .url(URL)
                .build();

        when(urlRepository.findByUrlIgnoreCase(anyString()))
                .thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class)))
                .thenReturn(url);

        urlService.convertUrlToShort(requestUrlBody);

        verify(urlRepository)
                .findByUrlIgnoreCase(anyString());
        verify(hashCache)
                .getHash();
        verify(urlRepository)
                .save(any(Url.class));
        verify(urlCacheRepository)
                .save(any(Url.class));
        verify(urlMapper)
                .toResponseBody(any(Url.class), anyString());
    }

    @Test
    @DisplayName("When hash exists in cache then not thrown exception")
    void whenHashExistsInCacheThenNotThrownException() {
        when(urlCacheRepository.findUrlInCacheByHash(anyString()))
                .thenReturn(URL);

        urlService.getFullRedirectionLink(HASH);

        verify(urlCacheRepository)
                .findUrlInCacheByHash(anyString());
        verify(urlRepository, times(0))
                .findByHashIgnoreCase(anyString());
    }

    @Test
    @DisplayName("When hash exists in db then not thrown exception")
    void whenHashExistsInDbThenNotThrownException() {
        when(urlCacheRepository.findUrlInCacheByHash(anyString()))
                .thenReturn(null);
        when(urlRepository.findByHashIgnoreCase(anyString()))
                .thenReturn(Optional.of(url));

        urlService.getFullRedirectionLink(HASH);

        verify(urlCacheRepository)
                .findUrlInCacheByHash(anyString());
        verify(urlRepository)
                .findByHashIgnoreCase(anyString());
    }

    @Test
    @DisplayName("When hash not exists then thrown exception")
    void whenHashNotExistsThenThrownException() {
        when(urlCacheRepository.findUrlInCacheByHash(anyString()))
                .thenReturn(null);
        when(urlRepository.findByHashIgnoreCase(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> urlService.getFullRedirectionLink(HASH));

        verify(urlCacheRepository)
                .findUrlInCacheByHash(anyString());
        verify(urlRepository)
                .findByHashIgnoreCase(anyString());
    }

    @Test
    @DisplayName("When method find by url is called then return optional entity")
    void whenMethodFindByUrlIsCalledThenReturnOptional() {
         urlService.findUrlInDatabaseByFullUrl(URL);

        verify(urlRepository)
                .findByUrlIgnoreCase(URL);
    }

    @Test
    @DisplayName("When method find by hash is called then return optional entity")
    void whenMethodFindByHashIsCalledThenReturnOptional() {
        urlService.findUrlInDatabaseByHash(HASH);

        verify(urlRepository)
                .findByHashIgnoreCase(HASH);
    }

    @Test
    @DisplayName("When method find in cache is called then return optional entity")
    void whenMethodFindInCacheIsCalledThenReturnOptional() {
        urlService.findUrlInCacheByHash(HASH);

        verify(urlCacheRepository)
                .findUrlInCacheByHash(HASH);
    }
}