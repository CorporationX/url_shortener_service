package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.FullUrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlMapper urlMapper = Mappers.getMapper(UrlMapper.class);

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    private final String originalUrl = "http://example.ru/123456";
    private final String hash = "kas4";

    @Test
    public void createShortUrlTest() {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(originalUrl);
        urlDto.setHash(hash);
        when(hashCache.getHash()).thenReturn(hash);

        String hash = urlService.createShortUrl(urlDto);

        assertEquals(urlDto.getHash(), hash);
    }

    @Test
    public void getUrlInCacheTest() {
        when(urlCacheRepository.findByHash(hash)).thenReturn(originalUrl);

        String result = urlService.getUrl(hash);

        assertEquals(originalUrl, result);
    }

    @Test
    public void getUrlInDBTest() {
        Url url = new Url();
        url.setUrl(originalUrl);
        when(urlCacheRepository.findByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        String result = urlService.getUrl(hash);

        assertEquals(originalUrl, result);
    }

    @Test
    public void getUrlNotFoundTest() {
        assertThrows(FullUrlNotFoundException.class, () -> urlService.getUrl(hash));

        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
    }

    @Test
    public void removeOldUrlsTest() {
        List<String> hashesString = List.of(hash);
        when(urlRepository.deleteAndGetOldUrls(any(LocalDate.class))).thenReturn(hashesString);

        urlService.removeOldUrls();

        verify(urlRepository).deleteAndGetOldUrls(any(LocalDate.class));
        verify(hashRepository).saveAll(anyList());
        verify(urlCacheRepository).deleteHashes(hashesString);
    }
}