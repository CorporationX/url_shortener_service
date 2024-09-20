package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.db.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;
    @Spy
    private UrlMapperImpl urlMapper;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    private final String baseUrl = "baseUrl/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", baseUrl);
    }

    @Test
    void test_makeShortLink() {
        String url = "url";
        UrlDto testUrlDto = UrlDto.builder()
                .url(url)
                .build();
        String hash = "1";

        when(urlRepository.findByUrl(url)).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(Mockito.any(Url.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UrlDto resultDto = urlService.makeShortLink(testUrlDto);

        assertEquals(baseUrl + hash, resultDto.getUrl());
        verify(urlRepository, times(1)).findByUrl(url);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(Mockito.any(Url.class));
        verify(urlCacheRepository, times(1)).save(hash, url);
        verifyNoMoreInteractions(urlRepository, hashCache, urlCacheRepository);
    }

    @Test
    void test_makeShortLink_alreadyExists_returnsExisting() {
        String hash = "1";
        String url = "url";
        Url testUrl = Url.builder()
                .hash(hash)
                .url(url)
                .createdAt(null)
                .build();
        UrlDto testUrlDto = UrlDto.builder()
                .url(url)
                .build();

        when(urlRepository.findByUrl(url)).thenReturn(Optional.of(testUrl));

        UrlDto resultDto = urlService.makeShortLink(testUrlDto);

        assertEquals(baseUrl + hash, resultDto.getUrl());
        verify(urlRepository, times(1)).findByUrl(url);
        verifyNoMoreInteractions(urlRepository, hashCache, urlCacheRepository);
    }

    @Test
    void getOriginalUrl_cached_returnFromCache() {
        String hash = "1";
        String url = "url";

        when(urlCacheRepository.getUrl(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(url, result);
        verify(urlCacheRepository, times(1)).getUrl(hash);
        verifyNoMoreInteractions(urlRepository, hashCache, urlCacheRepository);
    }

    @Test
    void getOriginalUrl_notCachedExistInDb_returnFromDb() {
        String hash = "1";
        String url = "url";
        Url urlFromDb = Url.builder()
                .hash(hash)
                .url(url)
                .build();
        when(urlCacheRepository.getUrl(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(urlFromDb));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(url, result);
        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).findById(hash);
        verify(urlCacheRepository, times(1)).save(hash, url);
        verifyNoMoreInteractions(urlRepository, hashCache, urlCacheRepository);
    }

    @Test
    void getOriginalUrl_notExists_throws() {
        String hash = "1";
        when(urlCacheRepository.getUrl(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.getOriginalUrl(hash));

        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).findById(hash);
        verifyNoMoreInteractions(urlRepository, hashCache, urlCacheRepository);
    }
}