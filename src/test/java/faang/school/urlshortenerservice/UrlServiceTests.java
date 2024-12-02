package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UrlServiceTests {

    @InjectMocks
    private UrlServiceImpl urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private UrlDto urlDto = new UrlDto("https://");
    private Url urlEntity = new Url().builder()
            .hash("hash")
            .url("https://")
            .build();

    @Test
    void testToShortLink() {
        when(hashCache.getHash()).thenReturn("hash");
        UrlDto urlDto1 = urlService.toShortUrl(urlDto);

        verify(urlRepository).save(urlEntity);
        verify(urlCacheRepository).save("hash", urlEntity);
        assertEquals(new UrlDto("https://urlshortener/hash"), urlDto1);
    }

    @Test
    void testGetUrl() {
        when(urlCacheRepository.getUrl("hash")).thenReturn(urlEntity);
        Url url = urlService.getUrl("hash");

        verify(urlCacheRepository).getUrl("hash");
        assertEquals("https://", url.getUrl());
    }

    @Test
    void testGetUrlNotFoundForHash() {
        when(urlCacheRepository.getUrl("hash")).thenReturn(null);
        assertThrows(UrlNotFoundException.class, () -> urlService.getUrl("hash"));
        verify(urlRepository).findById("hash");
    }

    @Test
    void testJobForCleanerScheduler() {
        urlService.jobForCleanerScheduler(1);
        verify(hashRepository).saveAll(List.of());
    }
}
