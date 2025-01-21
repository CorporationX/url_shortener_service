package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCacheService hashCacheService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlRedisCacheService urlRedisCacheService;

    @InjectMocks
    private UrlService urlService;

    private String urlPrefix;
    private LongUrlDto longUrlDto;
    private String longUrl;
    private String hash;
    private Url url;
    private ShortUrlDto shortUrlDto;
    private LocalDateTime expirationDate;
    private List<Hash> expectedHashes;
    private List<String> expectedHashesAsStrings;

    @BeforeEach
    void setUp() {
        urlPrefix = "prefix";
        longUrl = "http://longurl";
        longUrlDto = new LongUrlDto(longUrl);
        hash = "HASHHH";
        url = Url.builder()
                .hash(hash)
                .url(longUrl)
                .build();
        ReflectionTestUtils.setField(urlService, "urlPrefix", urlPrefix);
        shortUrlDto = new ShortUrlDto(urlPrefix + hash);
        expirationDate = LocalDateTime.now();
        expectedHashes = new ArrayList<>(
                List.of(Hash.builder().hash("1").build(),
                        Hash.builder().hash("2").build(),
                        Hash.builder().hash("3").build()));
        expectedHashesAsStrings = new ArrayList<>(List.of("1", "2", "3"));
    }

    @Test
    void testCreateShortUrlSuccess() {
        when(hashCacheService.getHash()).thenReturn(hash);

        ShortUrlDto result = urlService.createShortUrl(longUrlDto);

        verify(hashCacheService, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlRedisCacheService, times(1)).saveUrl(hash, longUrl);
        assertThat(result).isEqualTo(shortUrlDto);
    }

    @Test
    void testGetLongUrlSuccess_FromCache() {
        when(urlRedisCacheService.findByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrl(hash);

        verify(urlRedisCacheService, times(1)).findByHash(hash);
        verifyNoInteractions(urlRepository);
        assertThat(result).isEqualTo(longUrl);
    }

    @Test
    void testGetLongUrlSuccess_FromDatabase() {
        when(urlRedisCacheService.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrl(hash);

        verify(urlRedisCacheService, times(1)).findByHash(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verify(urlRedisCacheService, times(1)).saveUrl(hash, longUrl);
        assertThat(result).isEqualTo(longUrl);
    }

    @Test
    void testGetLongUrl_ThrowsException() {
        when(urlRedisCacheService.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getLongUrl(hash))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("was not found in cache and database");

        verify(urlRedisCacheService, times(1)).findByHash(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verify(urlRedisCacheService, never()).saveUrl(hash, longUrl);
    }

    @Test
    void testDeleteExpiredLinks() {
        when(urlRepository.deleteExpiredLinks(expirationDate)).thenReturn(expectedHashesAsStrings);

        List<Hash> result = urlService.deleteExpiredLinks(expirationDate);

        verify(urlRepository, times(1)).deleteExpiredLinks(expirationDate);
        assertThat(result).isEqualTo(expectedHashes);
    }
}