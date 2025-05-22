package faang.school.urlshortenerservice.unit.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestDto;
import faang.school.urlshortenerservice.dto.ResponseDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    UrlService urlService;

    @Mock
    UrlRepository urlRepository;

    @Mock
    HashCache localHash;

    @Mock
    CacheManager redisCacheManager;

    @Mock
    HashRepository hashRepository;

    @Mock
    Cache redisCache;

    @Captor
    private ArgumentCaptor<List<Hash>> hashCaptor;

    private final RequestDto dto = new RequestDto("https://youtube.com");
    private final String validHash = "1000";
    private final String invalidHash = "0012";
    private final Url url = Url.builder().url(dto.url()).hash(validHash).build();
    private final List<String> oldHashes = List.of("1000","1001","abc123");

    @Test
    void save_shouldReturnGeneratedHash() {
        when(urlRepository.save(any(Url.class))).thenReturn(url);
        when(localHash.getHash()).thenReturn(validHash);
        when(redisCacheManager.getCache("hashToUrl")).thenReturn(redisCache);

        ResponseDto response = urlService.save(dto);

        assertEquals(validHash, response.getHash());

        verify(localHash, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(redisCache).put(validHash, dto.url());
    }

    @Test
    void get_shouldReturnUrl() {
        when(urlRepository.findByHash(validHash)).thenReturn(Optional.of(url));

        assertEquals(urlService.get(validHash), url.getUrl());

        verify(urlRepository, times(1)).findByHash(validHash);
    }

    @Test
    void get_shouldThrowEntityNotFoundException() {
        when(urlRepository.findByHash(invalidHash)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> urlService.get(invalidHash));

        verify(urlRepository, times(1)).findByHash(invalidHash);
    }

    @Test
    void deleteUnusedHash_shouldDeleted(){
        when(urlRepository.deleteUnusedHashes()).thenReturn(oldHashes);

        urlService.deleteUnusedHashes();

        verify(hashRepository,times(1)).saveAll(hashCaptor.capture()); // ⬅ тут мы ловим переданные аргументы

        List<Hash> captured = hashCaptor.getValue();

        assertEquals(3, captured.size());
        assertEquals("1000", captured.get(0).getHash());
    }
}
