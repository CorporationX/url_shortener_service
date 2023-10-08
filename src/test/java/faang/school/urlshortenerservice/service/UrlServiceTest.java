package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;


    @Test
    void testShorten() {
        UrlDto urlDto = UrlDto.builder().url("https://google.com").build();
        Url url = Url.builder().url(urlDto.getUrl()).hash("1").build();

        Mockito.when(hashCache.getHash()).thenReturn(new Hash("1"));
        Mockito.when(urlRepository.save(url)).thenReturn(url);

        String actual = urlService.shorten(urlDto);

        Mockito.verify(urlRepository, Mockito.times(1)).save(url);
        Mockito.verify(urlCacheRepository, Mockito.times(1)).save(url);

        assertEquals("1", actual);
    }

    @Test
    void testGetUrl_ExistsRepository() {
        Mockito.when(urlCacheRepository.getUrl("1")).thenReturn("https://google.com");

        assertEquals("https://google.com", urlService.getUrl("1"));
    }

    @Test
    void testGetUrl_NotExistRepository() {
        Url url = Url.builder().url("https://google.com").build();
        Mockito.when(urlCacheRepository.getUrl("1")).thenReturn(null);

        Mockito.when(urlRepository.findByHash("1")).thenReturn(Optional.ofNullable(url));

        String actual = urlService.getUrl("1");

        assertEquals("https://google.com", actual);
    }

    @Test
    void testGetUrl_ThrowsEntityNotFoundException() {
        Mockito.when(urlCacheRepository.getUrl("1")).thenReturn(null);
        Mockito.when(urlRepository.findByHash("1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> urlService.getUrl("1"));
    }
}