package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.generator.HashCache;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
  @Mock
  private HashCache hashCash;
  @Mock
  private UrlRepository urlRepository;
  @Mock
  private UrlCacheService urlCacheService;
  @Mock
  private MessageSource messageSource;
  @InjectMocks
  private UrlService urlService;
  @Captor
  private ArgumentCaptor<Url> urlCaptor;
  private String hash;
  private String url;
  private Url urlEntity;

  @BeforeEach
  void setUp() {
    hash = "/aaaaaa";
    url = "/long_url";
    LocalDateTime createAt = LocalDateTime.now();
    urlEntity = Url.builder()
            .id(1L)
            .url(url)
            .hash(hash)
            .createdAt(createAt)
            .build();
  }

  @Test
  void testGetLongUrlFromCache() {
    // given
    when(urlCacheService.getCachedLongUrl(hash)).thenReturn(url);
    // when
    String urlActual = urlService.getLongUrl(hash);
    // then
    verify(urlRepository, times(0)).findByHash(hash);
    assertEquals(url, urlActual);
  }

  @Test
  void testGetLongUrlFromDB() {
    // given
    when(urlCacheService.getCachedLongUrl(hash)).thenReturn(null);
    when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlEntity));
    // when
    String urlActual = urlService.getLongUrl(hash);
    // then
    verify(urlRepository, times(1)).findByHash(hash);
    assertEquals(url, urlActual);
  }

  @Test
  void testGetLongUrlThrowException() {
    // given
    when(urlCacheService.getCachedLongUrl(hash)).thenReturn(null);
    when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());
    // then
    assertThrows(EntityNotFoundException.class, () -> urlService.getLongUrl(hash));
  }

  @Test
  void getShortUrl() {
    // given
    UrlDto urlDto = new UrlDto(url);
    when(hashCash.getHash()).thenReturn(hash);
    Url urlEntity = Url.builder()
            .url(url)
            .hash(hash)
            .build();
    // when
    String shortUrl = urlService.getHash(urlDto);
    // then
    verify(urlRepository, times(1)).save(urlCaptor.capture());
    assertEquals(urlEntity, urlCaptor.getValue());
    assertEquals(hash, shortUrl);
  }
}