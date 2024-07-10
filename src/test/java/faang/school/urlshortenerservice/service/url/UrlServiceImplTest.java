package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.hash.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlMapper urlMapper;

    @InjectMocks
    private UrlServiceImpl urlService;

    private URL url;
    private final String expectedUrl = "http://example.com";
    private final String hash = "abc123";
    private final Url entity = new Url();
    private final UrlDto expectedDto = new UrlDto();

    @BeforeEach
    void setUp() throws MalformedURLException {
        url = new URL(expectedUrl);
        entity.setHash(hash);
        entity.setUrl(url);
        expectedDto.setHash(hash);
        expectedDto.setUrl(expectedUrl);
    }

    @Test
    void createUrlHash_savesAndReturnsUrlDto() {
        when(hashCache.pop()).thenReturn(hash);
        when(urlMapper.toEntity(url, hash)).thenReturn(entity);
        when(urlRepository.saveAndFlush(entity)).thenReturn(entity);
        when(urlMapper.toDto(entity)).thenReturn(expectedDto);

        UrlDto result = urlService.createUrlHash(url);

        verify(urlCacheRepository, times(1)).saveUrlByHash(hash, url.toString());
        assertEquals(expectedDto, result);
    }

    @Test
    void getUrlFromHash_returnsUrlForExistingHash() {
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(entity));

        String result = urlService.getUrlFromHash(hash);

        assertEquals(expectedUrl, result);
    }

    @Test
    void getUrlFromHash_throwsNotFoundExceptionForMissingHash() {
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.getUrlFromHash(hash));
    }

    @Test
    void getUrlFromHash_returnsUrlFromCacheIfExists() {
        when(urlCacheRepository.getUrlByHash(hash)).thenReturn(Optional.of(expectedUrl));

        String result = urlService.getUrlFromHash(hash);

        verify(urlRepository, never()).findById(hash);
        assertEquals(expectedUrl, result);
    }
}