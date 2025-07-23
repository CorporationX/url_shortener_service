package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.dto.UrlEncodeDto;
import faang.school.urlshortenerservice.exception.InvalidUrlFormatException;
import faang.school.urlshortenerservice.exception.UrlNotFound;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    public static final String EXPECTED_URL = "https://example.com";
    public static final String HASH = "hash";

    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashCache hashCache;
    @Spy
    private UrlMapperImpl urlMapper;
    @Spy
    private Utils utils;
    @InjectMocks
    private UrlService urlService;

    /**
     * Тестируем метод getUrlByHash
     */
    @Test
    public void testGetUrlByHashWhenUrlExistsInCache() {
        String expectedUrl = EXPECTED_URL;
        when(urlCacheRepository.findByHash(anyString())).thenReturn(Optional.of(expectedUrl));

        String actualUrl = urlService.getUrlByHash(anyString());

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testGetUrlByHashWhenUrlDoesntExistInCacheButExistsInRepository() {
        when(urlCacheRepository.findByHash(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findByHash(HASH))
            .thenReturn(Optional.of(getMockUrl(HASH, EXPECTED_URL)));

        String actualUrl = urlService.getUrlByHash(HASH);

        assertEquals(EXPECTED_URL, actualUrl);
    }

    @Test
    public void testGetUrlByHashWhenRedisConnectionFailure() {
        when(urlCacheRepository.findByHash(anyString())).thenThrow(RedisConnectionFailureException.class);
        when(urlRepository.findByHash(HASH))
            .thenReturn(Optional.of(getMockUrl(HASH, EXPECTED_URL)));

        String actualUrl = urlService.getUrlByHash(HASH);

        assertEquals(EXPECTED_URL, actualUrl);
    }

    @Test
    public void testGetUrlByHashThrowsUrlNotFoundIfNoUrlIsFound() {
        String nonExistentHash = "nonexistent_hash";
        when(urlCacheRepository.findByHash(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findByHash(anyString())).thenReturn(Optional.empty());

        UrlNotFound actualException = assertThrows(UrlNotFound.class,
            () -> urlService.getUrlByHash(nonExistentHash));

        String expectedMessage = utils.format(UrlService.URL_NOT_FOUND, nonExistentHash);
        assertEquals(expectedMessage, actualException.getMessage());
    }

    /**
     * Тестируем метод encodeUrl
     */
    @Test
    public void testEncodeUrlWithInvalidUrl() {
        when(utils.isUrlValid(anyString())).thenReturn(false);

        InvalidUrlFormatException resultException = assertThrows(InvalidUrlFormatException.class,
            () -> urlService.encodeUrl(new UrlEncodeDto("invalid_url")));

        assertEquals(UrlService.INVALID_URL, resultException.getMessage());
    }

    @Test
    public void testEncodeUrlForExistingUrlInCache() {
        when(urlCacheRepository.findByUrl(anyString())).thenReturn(Optional.of(HASH));

        assertThat(urlService.encodeUrl(new UrlEncodeDto(EXPECTED_URL))).isEqualTo(HASH);
    }

    @Test
    public void testEncodeUrlGeneratesAndStoresNewHash() {
        when(utils.isUrlValid(EXPECTED_URL)).thenReturn(true);
        when(urlCacheRepository.findByUrl(EXPECTED_URL)).thenReturn(Optional.empty());
        when(urlRepository.findByUrl(EXPECTED_URL)).thenReturn(Optional.empty());
        when(hashCache.getNewHash()).thenReturn(HASH);

        UrlEncodeDto urlEncodeDto = new UrlEncodeDto(EXPECTED_URL);

        String actualHash = urlService.encodeUrl(urlEncodeDto);
        assertEquals(HASH, actualHash);

        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).addUrl(any(UrlDto.class));
    }

    @Test
    public void testEncodeUrlGeneratesAndStoresNewHashWhenRedisConnectionFailure() {
        when(utils.isUrlValid(EXPECTED_URL)).thenReturn(true);
        when(urlCacheRepository.findByUrl(EXPECTED_URL)).thenThrow(RedisConnectionFailureException.class);
        when(urlRepository.findByUrl(EXPECTED_URL)).thenReturn(Optional.empty());
        when(hashCache.getNewHash()).thenReturn(HASH);
        doThrow(RedisConnectionFailureException.class).when(urlCacheRepository).addUrl(any(UrlDto.class));

        UrlEncodeDto urlEncodeDto = new UrlEncodeDto(EXPECTED_URL);

        String actualHash = urlService.encodeUrl(urlEncodeDto);
        assertEquals(HASH, actualHash);

        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).addUrl(any(UrlDto.class));
    }

    /**
     * Тестируем метод clearOldUrls
     */
    @Test
    public void testClearOldUrlsRemovesExpiredRecordsFromDatabase() {
        int interval = 3;
        String firstHash = "hash1";
        String secondHash = "hash2";
        String firstUrl = "https://expired-url1.com";
        String secondUrl = "https://expired-url2.com";
        List<Url> expiredUrls = List.of(
            getMockUrl(firstHash, firstUrl),
            getMockUrl(secondHash, secondUrl)
        );
        when(urlRepository.clearOldUrls(interval)).thenReturn(expiredUrls);

        urlService.clearOldUrls(interval);

        verify(urlCacheRepository).deleteHashes(List.of(firstHash, secondHash));
        verify(urlCacheRepository).deleteUrls(List.of(firstUrl, secondUrl));
        verify(hashRepository).saveAll(anyList());
    }
    
    @Test
    public void testClearOldUrlsRemovesExpiredRecordsFromDatabaseWhenRedisConnectionFailure() {
        int interval = 3;
        String firstHash = "hash1";
        String secondHash = "hash2";
        String firstUrl = "https://expired-url1.com";
        String secondUrl = "https://expired-url2.com";
        List<Url> expiredUrls = List.of(
            getMockUrl(firstHash, firstUrl),
            getMockUrl(secondHash, secondUrl)
        );
        when(urlRepository.clearOldUrls(interval)).thenReturn(expiredUrls);
        doThrow(RedisConnectionFailureException.class).when(urlCacheRepository).deleteHashes(anyList());

        urlService.clearOldUrls(interval);

        verify(urlCacheRepository).deleteHashes(List.of(firstHash, secondHash));
        verify(urlCacheRepository, times(0)).deleteUrls(List.of(firstUrl, secondUrl));
        verify(hashRepository).saveAll(anyList());
    }
    
    

    private Url getMockUrl(String hash, String url) {
        return Url.builder()
            .hash(hash)
            .url(url)
            .build();
    }
}