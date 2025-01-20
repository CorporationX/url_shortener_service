package faang.school.urlshortenerservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

  @InjectMocks
  private UrlServiceImpl urlService;
  @Mock
  private HashCache hashCache;
  @Mock
  private UrlRepository urlRepository;
  @Mock
  private UrlCacheRepository urlCacheRepository;
  @Spy
  private UrlMapper urlMapper = Mappers.getMapper(UrlMapper.class);
  @Captor
  private ArgumentCaptor<Url> urlArgumentCaptor;

  private static final String HASH_STRING = "q7j4";
  private static final String URL_STRING = "https://google.com";

  @Test
  @DisplayName("Return url dto (validated by controller)")
  void testCreateShortUrl() {
    UrlCreateDto dto = UrlCreateDto.builder()
        .url(URL_STRING)
        .build();

    Url url = Url.builder()
        .url(URL_STRING)
        .hash(HASH_STRING)
        .build();

    when(hashCache.getHash()).thenReturn(HASH_STRING);
    when(urlRepository.create(dto.url(), HASH_STRING)).thenReturn(url);

    UrlResponseDto responseDto = urlService.createShortUrl(dto);

    verify(hashCache, times(1)).getHash();
    verify(urlRepository, times(1)).create(dto.url(), HASH_STRING);
    verify(urlCacheRepository, times(1)).add(urlArgumentCaptor.capture());

    Url urlCaptured = urlArgumentCaptor.getValue();

    assertEquals(URL_STRING, urlCaptured.getUrl());
    assertEquals(URL_STRING, responseDto.url());
  }

  @Test
  @DisplayName("Should return url from Redis when it is stored in Redis")
  void testGetOriginalUrlFromRedis() {
    when(urlCacheRepository.findUrlByHash(HASH_STRING)).thenReturn(URL_STRING);
    String result = urlService.getOriginalUrl(HASH_STRING);

    verify(urlCacheRepository, times(1)).findUrlByHash(HASH_STRING);
    assertEquals(URL_STRING, result);
  }

  @Test
  @DisplayName("Should return url from DB when it is stored in DB, not in Redis")
  void testGetOriginalUrlFromDB() {
    Url url = Url.builder()
        .hash(HASH_STRING)
        .url(URL_STRING)
        .build();

    when(urlCacheRepository.findUrlByHash(HASH_STRING)).thenReturn(null);
    when(urlRepository.findById(HASH_STRING)).thenReturn(Optional.of(url));
    String result = urlService.getOriginalUrl(HASH_STRING);

    verify(urlRepository, times(1)).findById(HASH_STRING);
    assertEquals(URL_STRING, result);
  }

  @Test
  @DisplayName("Should throw UrlNotFoundException when url does not exist in Redis, DB")
  void testNegativeGetOriginalUrlMissing() {
    when(urlCacheRepository.findUrlByHash(HASH_STRING)).thenReturn(null);
    when(urlRepository.findById(HASH_STRING)).thenThrow(
        new UrlNotFoundException("No such URL found"));

    var exception = assertThrows(UrlNotFoundException.class,
        () -> urlService.getOriginalUrl(HASH_STRING));

    verify(urlRepository, times(1)).findById(HASH_STRING);
    assertEquals("No such URL found", exception.getMessage());
  }

}