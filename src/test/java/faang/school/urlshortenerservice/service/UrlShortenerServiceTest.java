package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.entity.UrlMapping;
import faang.school.urlshortenerservice.enums.HashStatus;
import faang.school.urlshortenerservice.repository.RedisShortenerRepository;
import faang.school.urlshortenerservice.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private RedisShortenerRepository redisShortenerRepository;

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @Mock
    private HashPoolService hashPoolService;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    private String longUrl;
    private String hash;
    private UrlMapping mapping;

    @BeforeEach
    void setUp() {
        longUrl = "http://longurl.com";
        hash = "abc123";
        mapping = new UrlMapping(hash, longUrl, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
    }

    @Test
    void generateShortUrl_ShouldGenerateHashAndSaveToDbAndRedis() {
        FreeHash freeHash = new FreeHash(hash);
        int ttlMinutes = 10;

        when(hashPoolService.getAvailableHash()).thenReturn(freeHash);

        when(urlMappingRepository.save(any(UrlMapping.class))).thenReturn(mapping);

        FreeHash result = urlShortenerService.generateShortUrl(longUrl, ttlMinutes);

        assertEquals(freeHash.getHash(), result.getHash());

        ArgumentCaptor<UrlMapping> captor = ArgumentCaptor.forClass(UrlMapping.class);
        verify(urlMappingRepository).save(captor.capture());

        UrlMapping capturedMapping = captor.getValue();
        assertEquals(mapping.getHash(), capturedMapping.getHash());
        assertEquals(mapping.getLongUrl(), capturedMapping.getLongUrl());
    }

    @Test
    void resolveLongUrl_ShouldReturnLongUrlFromRedis() {
        when(redisShortenerRepository.getLongUrl(hash)).thenReturn(longUrl);

        String result = urlShortenerService.resolveLongUrl(hash);

        assertEquals(longUrl, result);
    }

    @Test
    void resolveLongUrl_ShouldReturnLongUrlFromDbIfNotFoundInRedis() {
        when(redisShortenerRepository.getLongUrl(hash)).thenReturn(null);
        when(urlMappingRepository.findByHashThrow(hash)).thenReturn(mapping);

        String result = urlShortenerService.resolveLongUrl(hash);

        assertEquals(longUrl, result);

        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(redisShortenerRepository).saveShortUrl(
                eq(hash), eq(longUrl), ttlCaptor.capture()
        );

        assertThat(ttlCaptor.getValue()).isPositive();
    }

    @Test
    void resolveLongUrl_ShouldThrowExceptionIfUrlIsInactive() {
        mapping.setStatus(HashStatus.WAITING);

        when(redisShortenerRepository.getLongUrl(hash)).thenReturn(null);
        when(urlMappingRepository.findByHashThrow(hash)).thenReturn(mapping);

        assertThrows(IllegalStateException.class, () -> urlShortenerService.resolveLongUrl(hash));
    }

    @Test
    void resolveLongUrl_ShouldThrowExceptionIfUrlIsExpired() {
        mapping.setStatus(HashStatus.ACTIVE);
        mapping.setExpiredAt(LocalDateTime.now().minusMinutes(5));

        when(redisShortenerRepository.getLongUrl(hash)).thenReturn(null);
        when(urlMappingRepository.findByHashThrow(hash)).thenReturn(mapping);

        assertThrows(IllegalStateException.class, () -> urlShortenerService.resolveLongUrl(hash));
    }
}