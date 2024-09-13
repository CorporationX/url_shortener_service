package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private  UrlValidator urlValidator;
    @Mock
    private  UrlRepository urlRepository;
    @Mock
    private  UrlCacheRepository urlCacheRepository;
    @Mock
    private  HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    Url url;
    UrlDto urlDto;

    @BeforeEach
    void init() {
        url = Url.builder().hash("h1").url("https://chatgpt.com/").build();
        urlDto = UrlDto.builder().url("https://chatgpt.com/").build();
    }

    @Test
    @DisplayName("Get long url : Wrong hash")
    public void testGetUrlWrongHash() {
        when(urlCacheRepository.get("55b")).thenReturn(Optional.empty());
        when(urlRepository.findById("55b")).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, ()-> urlService.getUrl("55b"));

        verifyNoMoreInteractions(urlCacheRepository, urlRepository);
        assertEquals("Couldn't find url for hash 55b", exception.getMessage());
    }

    @Test
    @DisplayName("Get long url : Cache is empty but repo found")
    public void testGetUrlCacheEmpty() {
        when(urlCacheRepository.get("h1")).thenReturn(Optional.empty());
        when(urlRepository.findById("h1")).thenReturn(Optional.of(url));

        RedirectView result = urlService.getUrl("h1");

        assertEquals("https://chatgpt.com/", result.getUrl());
    }

    @Test
    @DisplayName("Create short url: Wrong url")
    public void testCreateShortUrlWrongUrl() {
        String errorMessage = "Please provide a valid URL";
        doThrow(new DataValidationException(errorMessage)).when(urlValidator).validateUrl(anyString());
        Exception exception = assertThrows(DataValidationException.class, ()-> urlService.createShortUrl(urlDto));

        verifyNoMoreInteractions(urlCacheRepository, hashCache, urlCacheRepository);

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Create short url: All ok")
    public void testCreateShortUrl() {
        doNothing().when(urlValidator).validateUrl(anyString());
        when(hashCache.getHash()).thenReturn("365d");

        UrlDto result = urlService.createShortUrl(urlDto);

        verify(urlCacheRepository, times(1)).put(anyString(), anyString());
        verify(urlRepository, times(1)).save(ArgumentMatchers.any());

        assertEquals("365d", result.getUrl());
    }
}