package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlRedisCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlRedisCacheServiceTest {

    @Mock
    private UrlRedisCacheRepository urlRedisCacheRepository;

    @InjectMocks
    private UrlRedisCacheService urlRedisCacheService;

    private String hash;
    private String longUrl;

    @BeforeEach
    void setUp() {
        hash = "HASHHH";
        longUrl = "LONG URL";
    }

    @Test
    void testSaveUrl() {
        urlRedisCacheService.saveUrl(hash, longUrl);

        verify(urlRedisCacheRepository, times(1)).saveUrl(hash, longUrl);
    }

    @Test
    void testGetUrl() {
        when(urlRedisCacheRepository.findByHash(hash)).thenReturn(Optional.of(longUrl));

        Optional<String> result = urlRedisCacheService.findByHash(hash);

        verify(urlRedisCacheRepository, times(1)).findByHash(hash);
        assertThat(result).isEqualTo(Optional.of(longUrl));
    }
}