package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.CachedUrl;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {

    @InjectMocks
    private UrlServiceImpl urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private String anyString;
    private String anyAnotherString;

    @BeforeEach
    public void init() throws Exception {
        anyString = "anyString";
        anyAnotherString = "anyAnotherString";

        Field serverHost = UrlServiceImpl.class.getDeclaredField("serverHost");
        serverHost.setAccessible(true);
        serverHost.set(urlService, "localhost");

        Field serverPort = UrlServiceImpl.class.getDeclaredField("serverPort");
        serverPort.setAccessible(true);
        serverPort.set(urlService, "8080");

        Field requestMapping = UrlServiceImpl.class.getDeclaredField("requestMapping");
        requestMapping.setAccessible(true);
        requestMapping.set(urlService, "/api/v1/urls");
    }

    @Test
    public void createShortUrl_SuccessfullyCreates() throws Exception {
        when(hashCache.getFreeHash()).thenReturn(anyString);

        assertEquals(
                new ShortUrlDto("https://localhost:8080/api/v1/urls/anyString"),
                urlService.createShortUrl(new CreateShortUrlDto(anyString)));

        verify(hashCache).getFreeHash();
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(any(CachedUrl.class));
    }

    @Test
    public void getOriginalUrl_ReturnsFromDb() {
        when(urlRepository.findByHash(anyString)).thenReturn(Optional.of(new Url(anyString, anyAnotherString)));

        assertEquals(anyAnotherString, urlService.getOriginalUrl(anyString));

        verify(urlCacheRepository).findById(anyString);
        verify(urlRepository).findByHash(anyString);
        verify(urlCacheRepository).save(any(CachedUrl.class));
    }

    @Test
    public void getOriginalUrl_NonExistentUrl() {
        assertThrows(InvalidUrlException.class, () -> urlService.getOriginalUrl(anyString));

        verify(urlCacheRepository).findById(anyString);
        verify(urlRepository).findByHash(anyString);
    }

    @Test
    public void getOriginalUrl_ReturnsFromCache() {
        when(urlCacheRepository.findById(anyString))
                .thenReturn(Optional.of(new CachedUrl(anyString, anyAnotherString)));

        assertEquals(anyAnotherString, urlService.getOriginalUrl(anyString));

        verify(urlCacheRepository).findById(anyString);
        verify(urlRepository, never()).findByHash(anyString);
        verify(urlCacheRepository, never()).save(any(CachedUrl.class));
    }
}
