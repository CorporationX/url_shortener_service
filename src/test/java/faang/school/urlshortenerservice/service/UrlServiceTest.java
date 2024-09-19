package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepositoryImpl;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    UrlRepository urlRepository;

    @Mock
    UrlCacheRepositoryImpl urlCacheRepositoryImpl;

    @Mock
    HashCache hashCache;

    @InjectMocks
    UrlService urlService;

    UrlDto testUrlDto = UrlDto.builder()
            .originalUrl("testUrl")
            .build();

    String testOriginalUrl = "testUrl";
    String testHash = "abc";

    Url testUrl = Url.builder()
            .hash("abc")
            .originalUrl("testUrl")
            .build();

    @Test
    void testShortenIfUrlExists() {
        when(urlRepository.findByOriginalUrl(testOriginalUrl)).thenReturn(Optional.of(testUrl));

        String testHash = urlService.shorten(testUrlDto);

        verify(urlRepository, times(1))
                .findByOriginalUrl(anyString());
        verify(urlCacheRepositoryImpl, times(0))
                .save(anyString(), anyString());
        assertEquals(testHash, testUrl.getHash());
    }

    @Test
    void testShortenIfUrlNotExists() {
        when(urlRepository.findByOriginalUrl(testOriginalUrl)).thenReturn(Optional. empty() );
        when(hashCache.getHash()).thenReturn(testHash);
        when(urlCacheRepositoryImpl.save(testHash, testOriginalUrl)).thenReturn(testUrl);

        String testHash = urlService.shorten(testUrlDto);

        verify(urlRepository, times(1))
                .findByOriginalUrl(anyString());
        verify(urlCacheRepositoryImpl, times(1))
                .save(anyString(), anyString());
        assertEquals(testHash, testUrl.getHash());
    }

    @Test
    void testGetOriginalUrlIfOriginalUrlIsNull() {
        when(urlCacheRepositoryImpl.getUrl(anyString())).thenReturn(new Url());

        assertThrows(NotFoundException.class, () -> urlService.getOriginalUrl(anyString()));
    }

    @Test
    void testGetOriginalUrlIfOriginalUrlNotNull() {
        when(urlCacheRepositoryImpl.getUrl(anyString())).thenReturn(testUrl);

        urlService.getOriginalUrl(anyString());

        verify(urlCacheRepositoryImpl, times(1))
                .getUrl(anyString());
    }
}