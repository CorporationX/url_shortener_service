package faang.school.url_shortener_service.service;

import faang.school.url_shortener_service.cache.HashCache;
import faang.school.url_shortener_service.dto.UrlRequestDto;
import faang.school.url_shortener_service.dto.UrlResponseDto;
import faang.school.url_shortener_service.entity.Url;
import faang.school.url_shortener_service.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisCacheManager cacheManager;

    @InjectMocks
    private UrlService urlService;

    @Value("${short.url.base}")
    private String baseUrl;
    @Value("${short.url.versionPath}")
    private String versionPath;

    private UrlRequestDto requestDto;
    private Url existingUrl;
    private Url newUrl;
    private String hash;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        hash = "abc123";
        baseUrl = "localhost:9081";
        versionPath = "/api/v1/url/";

        requestDto = new UrlRequestDto("https://example.com/long-url");

        existingUrl = new Url("xyz789", "https://example.com/long-url", OffsetDateTime.now());

        newUrl = new Url(hash, requestDto.getOriginalUrl(), OffsetDateTime.now());

        Field baseUrlField = UrlService.class.getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(urlService, baseUrl);
        Field versionPathField = UrlService.class.getDeclaredField("versionPath");
        versionPathField.setAccessible(true);
        versionPathField.set(urlService, versionPath);
    }

    @Test
    void createShortUrl_ShouldReturnExistingShortUrl_WhenUrlAlreadyExists() {
        when(urlRepository.findByUrl(requestDto.getOriginalUrl())).thenReturn(Optional.of(existingUrl));
        UrlResponseDto response = urlService.createShortUrl(requestDto);
        assertThat(response).isNotNull();
        assertThat(response.getShortUrl()).isEqualTo(baseUrl + versionPath + "xyz789");
        verify(hashCache, never()).getHash();
        verify(urlRepository, never()).save(any(Url.class));
    }

    @Test
    void createShortUrl_ShouldGenerateNewHash_WhenUrlDoesNotExist() {
        when(urlRepository.findByUrl(requestDto.getOriginalUrl())).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(any(Url.class))).thenReturn(newUrl);

        UrlResponseDto response = urlService.createShortUrl(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getShortUrl()).isEqualTo(baseUrl + versionPath + hash);

        verify(hashCache).getHash();
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void getOriginalURL_ShouldReturnOriginalUrl_WhenHashExists() {
        when(urlRepository.findById(hash)).thenReturn(Optional.of(newUrl));
        String originalUrl = urlService.getOriginalURL(hash);
        assertThat(originalUrl).isEqualTo(newUrl.getUrl());
        verify(urlRepository).findById(hash);
    }

    @Test
    void getOriginalURL_ShouldThrowException_WhenHashDoesNotExist() {
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> urlService.getOriginalURL(hash))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("URL with hash abc123 not found");
        verify(urlRepository).findById(hash);
    }
}
