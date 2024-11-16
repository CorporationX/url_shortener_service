package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.builder.UrlBuilder;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    private static final String SERVER_URL = "http://short.url";

    @Mock
    private HashCache hashCache;
    @Mock
    private HashRepository hashRepository;
    @Spy
    private UrlBuilder urlBuilder;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @InjectMocks
    private UrlService urlService;

    private final String originalUrl = "http://example.com/msdfigerjuhserewrlkfgkdfgjewuhrtherbugsdfslfkgjirhtughsdfgnjrgt";
    private final String hash = "ht6Y";
    private final String shortUrl = "http://short.url/ht6Y";


    private Url url;

    @BeforeEach
    void setUp() throws Exception {
        Field serverUrl = UrlBuilder.class.getDeclaredField("serverUrl");
        serverUrl.setAccessible(true);
        serverUrl.set(urlBuilder, SERVER_URL);

        url = Url.builder()
                .url(originalUrl)
                .hash(hash)
                .build();
    }

    @Test
    public void testCreateHashUrl() {
        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.createHashUrl(originalUrl);

        assertEquals(result, shortUrl);
        verify(urlBuilder).makeUrl(hash);
        verify(hashCache).getHash();
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(any(Url.class));
    }

    @Test
    public void testGetOriginalUrl() {
        when(urlRepository.findById(hash)).thenReturn(Optional.ofNullable(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(result, url.getUrl());
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
    }

    @Test
    public void testGetOriginalUrlRedisCache() {
        when(urlCacheRepository.findByHash(hash)).thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(result, originalUrl);
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository, never()).findById(anyString());
    }

    @Test
    public void testGetOriginalUrlNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> urlService.getOriginalUrl(hash));

        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
    }

    @Test
    public void testRemoveExpiredUrls() {
        List<String> hashesString = List.of(hash);
        when(urlRepository.getAndDeleteUrlsByDate(any(LocalDate.class))).thenReturn(hashesString);

        urlService.removeExpiredUrls();

        verify(urlRepository).getAndDeleteUrlsByDate(any(LocalDate.class));
        verify(hashRepository).saveAll(anyList());
        verify(urlCacheRepository).deleteHashes(hashesString);
    }
}
