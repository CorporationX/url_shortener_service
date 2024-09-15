package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.URLCacheRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private HashMapper hashMapper;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private URLCacheRepository urlCacheRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "periodConfig", "P1Y");
    }

    @Test
    public void testCreateShortLink_ExistingUrl() {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("http://new-url.com");
        Url url = new Url();
        url.setHash("existingHash");

        HashDto expectedHashDto = new HashDto("existingHash");

        when(urlValidator.validateUrlByAlreadyExists(urlDto.getUrl())).thenReturn(true);
        when(urlRepository.findByUrl(urlDto.getUrl())).thenReturn(url);
        when(hashMapper.toDto(any())).thenReturn(expectedHashDto);

        HashDto result = urlService.createShortLink(urlDto);

        assertEquals(expectedHashDto, result);
        verify(urlValidator).validateUrlByAlreadyExists(urlDto.getUrl());
        verify(urlRepository).findByUrl(urlDto.getUrl());
        verify(hashMapper).toDto(any());
        verifyNoMoreInteractions(urlCacheRepository);
    }

    @Test
    public void testCreateShortLink_NewUrl() {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("http://new-url.com");

        Url url = new Url();
        url.setHash("newHash");
        url.setUrl("http://new-url.com");

        HashDto expectedHashDto = new HashDto("newHash");

        when(urlValidator.validateUrlByAlreadyExists(urlDto.getUrl())).thenReturn(false);
        when(urlMapper.toEntity(urlDto)).thenReturn(url);
        when(hashCache.getHash()).thenReturn("newHash");
        when(urlRepository.save(url)).thenReturn(url);
        when(hashMapper.toDto(any())).thenReturn(expectedHashDto);

        HashDto result = urlService.createShortLink(urlDto);

        assertEquals(expectedHashDto, result);
        verify(urlValidator).validateUrlByAlreadyExists(urlDto.getUrl());
        verify(urlMapper).toEntity(urlDto);
        verify(hashCache).getHash();
        verify(urlRepository).save(url);
        verify(urlCacheRepository).save("newHash", "http://new-url.com");
        verify(hashMapper).toDto(any());
    }


    @Test
    public void testRemoveOldUrl() {
        String oldHash1 = "hash1";
        String oldHash2 = "hash2";
        List<String> freedHashes = Arrays.asList(oldHash1, oldHash2);

        when(urlRepository.deleteOldUrlsAndReturnHashes(any(LocalDateTime.class))).thenReturn(freedHashes);

        urlService.removeOldUrl();

        verify(urlRepository).deleteOldUrlsAndReturnHashes(any(LocalDateTime.class));
        verify(hashRepository).saveAll(anyList());
        verifyNoMoreInteractions(urlCacheRepository);
    }

    @Test
    public void testGetUrlByHash_CacheHit() {
        String hash = "existingHash";
        String expectedUrl = "http://cached-url.com";

        when(urlCacheRepository.getUrl(hash)).thenReturn(Optional.of(expectedUrl));

        String url = urlService.getUrlByHash(hash);

        assertEquals(expectedUrl, url);
        verify(urlCacheRepository).getUrl(hash);
        verifyNoMoreInteractions(urlRepository);
    }
}