package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.ResourceNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import faang.school.urlshortenerservice.repository.URLRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private URLRepository urlRepository;

    @Mock
    private URLCacheRepository urlCacheRepository;

    @Mock
    private UrlMapper urlMapper;

    private UrlDto urlDto;
    private Url url;

    @BeforeEach
    void setUp() {
        urlDto = new UrlDto();
        urlDto.setUrl("http://example.com");

        String generatedHash = "someHash";
        url = new Url();
        url.setHash(generatedHash);
        url.setUrl("http://example.com");
    }

    @Test
    void createShouldReturnHashWhenUrlIsCreatedSuccessfully() {
        when(hashCache.getHash()).thenReturn(url.getHash());
        when(urlMapper.toEntity(urlDto)).thenReturn(url);

        String hash = urlService.create(urlDto);

        assertEquals(url.getHash(), hash);
        verify(hashCache).getHash();
        verify(urlMapper).toEntity(urlDto);
        verify(urlRepository).save(url);
        verify(urlCacheRepository).save(url.getHash(), urlDto.getUrl());
    }

    @Test
    void getByHashShouldReturnUrlFromCacheWhenCacheIsPresent() {
        String hash = "someHash";
        String cachedUrl = "http://example.com";

        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(cachedUrl));

        String result = urlService.getByHash(hash);

        assertEquals(cachedUrl, result);
        verify(urlCacheRepository).get(hash);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void getByHashShouldReturnUrlFromRepositoryWhenCacheIsAbsent() {
        when(urlCacheRepository.get(url.getHash())).thenReturn(Optional.empty());
        when(urlRepository.findById(url.getHash())).thenReturn(Optional.of(url));

        String result = urlService.getByHash(url.getHash());

        assertEquals(url.getUrl(), result);
        verify(urlCacheRepository).get(url.getHash());
        verify(urlRepository).findById(url.getHash());
    }

    @Test
    void getByHashShouldThrowResourceNotFoundExceptionWhenHashNotFound() {
        when(urlCacheRepository.get(url.getHash())).thenReturn(Optional.empty());
        when(urlRepository.findById(url.getHash())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> urlService.getByHash(url.getHash()));
        assertEquals("Hash someHash doesn't have url", exception.getMessage());

        verify(urlCacheRepository).get(url.getHash());
        verify(urlRepository).findById(url.getHash());
    }

    @Test
    void deleteUrlByDateShouldReturnHashesOfDeletedUrls() {
        LocalDateTime date = LocalDateTime.now().minusYears(1);
        Url url1 = new Url();
        url1.setHash("hash1");
        Url url2 = new Url();
        url2.setHash("hash2");
        when(urlRepository.deleteByDate(date)).thenReturn(List.of(url1, url2));

        List<String> result = urlService.deleteUrlByDate(date);

        assertEquals(List.of("hash1", "hash2"), result);
        verify(urlRepository).deleteByDate(date);
    }
}
