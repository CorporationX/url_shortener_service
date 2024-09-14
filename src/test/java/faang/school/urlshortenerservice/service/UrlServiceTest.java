package faang.school.urlshortenerservice.service;

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

import java.time.LocalDateTime;

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
    }

    @Test
    public void testCreateShortLink_NewUrl() {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("http://new-url.com");
        Url url = new Url();
        url.setHash("newHash");

        HashDto expectedHashDto = new HashDto("newHash");

        when(urlValidator.validateUrlByAlreadyExists(urlDto.getUrl())).thenReturn(false);
        when(urlMapper.toEntity(urlDto)).thenReturn(url);
        when(hashCache.getHash()).thenReturn("newHash");
        when(hashMapper.toDto(any())).thenReturn(expectedHashDto);

        HashDto result = urlService.createShortLink(urlDto);

        assertEquals(expectedHashDto, result);
        verify(urlValidator).validateUrlByAlreadyExists(urlDto.getUrl());
        verify(urlMapper).toEntity(urlDto);
        verify(hashCache).getHash();
        verify(urlRepository).save(url);
        verify(hashMapper).toDto(any());
    }
}