package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cach.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void saveNewHash() {
        UrlDto urlDto = new UrlDto("http://example.com");
        Hash mockHash = new Hash("generatedHash");
        Url mockUrl = Url.builder().hash(mockHash.getHash()).url(urlDto.url()).build();

        when(hashCache.getHash()).thenReturn(mockHash);
        when(urlRepository.save(mockUrl)).thenReturn(mockUrl);

        String result = urlService.saveNewHash(urlDto);

        verify(urlRepository, times(1)).save(mockUrl);
        verify(urlCacheRepository, times(1)).addToRedis(mockUrl);
        assertEquals("generatedHash", result);

    }

    @Test
    void saveToDb() {
        Url mockUrl = Url.builder()
                .hash("generatedHash")
                .url("http://example.com")
                .build();

        urlService.saveToDb(mockUrl);

        verify(urlRepository, times(1)).save(mockUrl);
    }

    @Test
    void searchUrl() {
        String hash = "generatedHash";
        String cachedUrl = "http://cached-example.com";
        String dbUrl = "http://db-example.com";

        when(urlCacheRepository.searchInRedis(hash)).thenReturn(cachedUrl);
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.empty());

        String resultFromCache = urlService.searchUrl(hash);

        when(urlCacheRepository.searchInRedis(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.of(Url.builder()
                .hash(hash)
                .url(dbUrl)
                .build()));

        String resultFromDb = urlService.searchUrl(hash);
        assertEquals(cachedUrl, resultFromCache);
        assertEquals(dbUrl, resultFromDb);

        verify(urlCacheRepository, times(2)).searchInRedis(hash);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void negativeSaveNewHash() {
        UrlDto urlDto = new UrlDto("http://example.com");
        Hash mockHash = new Hash("generatedHash");
        Url mockUrl = Url.builder().hash(mockHash.getHash()).url(urlDto.url()).build();

        when(hashCache.getHash()).thenReturn(mockHash);
        when(urlRepository.save(mockUrl)).thenThrow(new RuntimeException("Ошибка при сохранении"));

        try {
            urlService.saveNewHash(urlDto);
        } catch (RuntimeException e) {
            assertEquals("Ошибка при сохранении", e.getMessage());
        }

        verify(urlRepository, times(1)).save(mockUrl);
        verify(urlCacheRepository, times(0)).addToRedis(mockUrl);
    }

    @Test
    void negativeSearchUrl() {
        String hash = "generatedHash";

        when(urlCacheRepository.searchInRedis(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.empty());

        try {
            urlService.searchUrl(hash);
        } catch (EntityNotFoundException e) {
            assertEquals("Урл не найден", e.getMessage());
        }

        verify(urlCacheRepository, times(1)).searchInRedis(hash);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void negativeSaveToDb_whenSaveFails() {
        Url mockUrl = Url.builder()
                .hash("generatedHash")
                .url("http://example.com")
                .build();

        when(urlRepository.save(mockUrl)).thenThrow(new RuntimeException("Ошибка при сохранении"));

        try {
            urlService.saveToDb(mockUrl);
        } catch (RuntimeException e) {
            assertEquals("Ошибка при сохранении", e.getMessage());
        }

        verify(urlRepository, times(1)).save(mockUrl);
    }
}