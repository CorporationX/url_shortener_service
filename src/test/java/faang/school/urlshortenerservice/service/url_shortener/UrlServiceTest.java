package faang.school.urlshortenerservice.service.url_shortener;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import faang.school.urlshortenerservice.repository.url_cash.impl.UrlCacheRepositoryImpl;
import faang.school.urlshortenerservice.service.hash_cache.HashCache;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlCacheRepositoryImpl urlCacheRepository;

    @Mock
    private UrlRepositoryImpl urlRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private String hash;
    private String url;
    private UrlDto urlDto;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        String domain = "http://localhost:8080/api/v1/urls/";
        hash = "hash";
        url = domain + hash;

        urlDto = UrlDto.builder()
                .url("https://LongUrl")
                .build();

        Field domainField = UrlService.class.getDeclaredField("domain");
        domainField.setAccessible(true);
        domainField.set(urlService, domain);
    }

    @Test
    public void shortenUrlTest() {
        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.shortenUrl(urlDto);

        verify(urlRepository).save(hash, urlDto.getUrl());
        verify(urlCacheRepository).saveUrl(hash, urlDto.getUrl());

        assertEquals(url, result);
    }

    @Test
    public void getOriginalUrlPresentInCacheRepositoryTest() {
        when(urlCacheRepository.getUrl(hash)).thenReturn(urlDto.getUrl());

        String originalUrl = urlService.getOriginalUrl(hash);

        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(0)).findOriginalUrlByHash(hash);
        verify(urlCacheRepository, times(0)).saveUrl(hash, originalUrl);

        assertEquals(urlDto.getUrl(), originalUrl);
    }

    @Test
    public void getOriginalUrlNotPresentInCacheRepositoryTest() {
        when(urlCacheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.findOriginalUrlByHash(hash)).thenReturn(Optional.ofNullable(urlDto.getUrl()));

        String originalUrl = urlService.getOriginalUrl(hash);

        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).findOriginalUrlByHash(hash);
        verify(urlCacheRepository, times(1)).saveUrl(hash, originalUrl);

        assertEquals(urlDto.getUrl(), originalUrl);
    }

    @Test
    public void shortenUrlThrowsExceptionTest() {
        when(urlCacheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.findOriginalUrlByHash(hash)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(hash));

        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).findOriginalUrlByHash(hash);
        verify(urlCacheRepository, times(0)).saveUrl(hash, urlDto.getUrl());
    }
}