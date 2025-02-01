package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlMapper urlMapper;
    @InjectMocks
    private UrlService urlService;

    @Test
    public void testProcessUrlSuccess() {
        String inputUrl = "http://example.com";
        String hash = "abc123";
        LocalDateTime createTime = LocalDateTime.now();
        Url urlEntity = Url.builder().url(inputUrl).hash(hash).createdAt(createTime).build();
        UrlResponseDto responseDto = new UrlResponseDto(hash, inputUrl, createTime);

        when(urlRepository.existsByUrl(inputUrl)).thenReturn(false);
        when(hashCache.getHash()).thenReturn(new Hash(hash));
        when(urlRepository.save(any(Url.class))).thenReturn(urlEntity);
        when(urlMapper.toDto(any(Url.class))).thenReturn(responseDto);

        UrlResponseDto result = urlService.processUrl(inputUrl);

        verify(urlRepository, Mockito.times(1)).existsByUrl(inputUrl);
        verify(hashCache, Mockito.times(1)).getHash();
        verify(urlRepository, Mockito.times(1)).save(any(Url.class));
        verify(urlMapper, Mockito.times(1)).toDto(any(Url.class));

        Assertions.assertEquals(responseDto.getHash(), result.getHash());
        Assertions.assertEquals(responseDto.getUrl(), result.getUrl());
        Assertions.assertEquals(responseDto.getCreateTime(), result.getCreateTime());
    }

    @Test
    public void testProcessUrlUrlAlreadyExists() {
        String inputUrl = "http://example.com";

        when(urlRepository.existsByUrl(inputUrl)).thenReturn(true);

        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> urlService.processUrl(inputUrl));
        Assertions.assertEquals("Url already exists: " + inputUrl, exception.getMessage());

        verify(urlRepository, Mockito.times(1)).existsByUrl(inputUrl);
        verify(urlMapper, Mockito.times(0)).toDto(any(Url.class));
        verifyNoInteractions(hashCache);
        verifyNoInteractions(urlMapper);
    }

    @Test
    public void testGetUrlByHashValuePresentInCache() {
        String hash = "abc123";
        String longUrl = "http://example.com";

        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getUrlByHash(hash);

        verify(urlCacheRepository).findByHash(hash);
        verifyNoInteractions(urlRepository);
        Assertions.assertEquals(longUrl, result);
    }

    @Test
    public void testGetUrlByHashValueNotPresentInCache() {
        String hash = "abc123";
        String longUrl = "http://example.com";

        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getUrlByHash(hash);

        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findByHash(hash);

        Assertions.assertEquals(longUrl, result);
    }

    @Test
    public void testGetUrlByHashNotFound() {
        String hash = "nonExistingHash";

        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getUrlByHash(hash));
        Assertions.assertEquals("URL not found for hash: " + hash, exception.getMessage());

        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findByHash(hash);
    }
}