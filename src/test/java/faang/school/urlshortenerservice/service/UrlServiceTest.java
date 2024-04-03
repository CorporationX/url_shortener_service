package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCache;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashCache hashCache;
    @Spy
    private UrlMapper urlMapper;

    @InjectMocks
    private UrlService urlService;

    private String hash = "hash1";
    private Url url;
    private String urlInput;
    private UrlDto urlDto;
    private UrlDto shortUrl;

    @BeforeEach
    void setUp() {
        urlInput = "https://ya.ru";
        url = Url.builder().hash(hash).url(urlInput).build();
        urlDto = new UrlDto(urlInput);
        shortUrl = new UrlDto("https://corpX.com/" + hash);
        ReflectionTestUtils.setField(urlService,"urlPattern", "https://corpX.com/");
    }

    @Test
    void testCreateShortUrl_whenHashExistInCash_thenReturnShortUrl() {
        //Arrange
        when(urlCacheRepository.findByUrl(urlInput)).thenReturn(new UrlCache(hash, urlInput));

        //Act
        UrlDto result = urlService.createShortUrl(urlDto);

        //Assert
        assertAll(
                () -> verify(urlCacheRepository, times(1)).findByUrl(urlInput),
                () -> verify(urlRepository, times(0)).findUrlByUrl(urlInput),
                () -> verify(hashCache, times(0)).getHash(),
                () -> verify(urlRepository, times(0)).save(any()),
                () -> verify(urlCacheRepository, times(0)).save(any()),
                () -> assertEquals(result, shortUrl)
        );
    }

    @Test
    void testCreateShortUrl_whenHashNotExistInCashExistInBd_thenReturnShortUrl() {
        //Arrange
        when(urlCacheRepository.findByUrl(urlInput)).thenReturn(null);
        when(urlRepository.findUrlByUrl(urlInput)).thenReturn(url);

        //Act
        UrlDto result = urlService.createShortUrl(urlDto);

        //Assert
        assertAll(
                () -> verify(urlCacheRepository, times(1)).findByUrl(urlInput),
                () -> verify(urlRepository, times(1)).findUrlByUrl(urlInput),
                () -> verify(hashCache, times(0)).getHash(),
                () -> verify(urlRepository, times(0)).save(any()),
                () -> verify(urlCacheRepository, times(1)).save(any()),
                () -> assertEquals(result, shortUrl)
        );
    }

    @Test
    void testCreateShortUrl_whenHashNotExist_thenCreateShortUrl() {
        //Arrange
        when(urlCacheRepository.findByUrl(urlInput)).thenReturn(null);
        when(urlRepository.findUrlByUrl(urlInput)).thenReturn(null);
        when(hashCache.getHash()).thenReturn(hash);

        //Act
        UrlDto result = urlService.createShortUrl(urlDto);

        //Assert
        assertAll(
                () -> verify(urlCacheRepository, times(1)).findByUrl(urlInput),
                () -> verify(urlRepository, times(1)).findUrlByUrl(urlInput),
                () -> verify(hashCache, times(1)).getHash(),
                () -> verify(urlRepository, times(1)).save(any()),
                () -> verify(urlCacheRepository, times(1)).save(any()),
                () -> assertEquals(result, shortUrl)
        );
    }

    @Test
    void testGetUrlByHash_whenUrlExistInCash_thenReturnFromCash() {
        //Arrange
        when(urlCacheRepository.findById(hash)).thenReturn(Optional.of(new UrlCache()));

        //Act
        urlService.getUrlByHash(hash);

        //Assert
        verify(urlCacheRepository, times(1)).findById(hash);
        verify(urlRepository, times(0)).findById(hash);
        verify(urlCacheRepository, times(0)).save(any());
    }

    @Test
    void testGetUrlByHash_whenUrlNotExistInCash_thenReturnFromBd() {
        //Arrange
        when(urlCacheRepository.findById(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        //Act
        urlService.getUrlByHash(hash);

        //Assert
        verify(urlCacheRepository, times(1)).findById(hash);
        verify(urlRepository, times(1)).findById(hash);
        verify(urlCacheRepository, times(1)).save(any());
    }

    @Test
    void testCleanHash() {
        //Arrange
        List<String> freeHashes = List.of("hash1", "hash2");
        when(urlRepository.deleteOldUrl()).thenReturn(freeHashes);

        //Act
        urlService.cleanHash();

        //Assert
        verify(urlRepository, times(1)).deleteOldUrl();
        verify(hashRepository, times(1)).saveAll(any());
    }
}