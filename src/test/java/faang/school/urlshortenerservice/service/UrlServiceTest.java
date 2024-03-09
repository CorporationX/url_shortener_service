package faang.school.urlshortenerservice.service;

import static org.mockito.Mockito.*;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    private UrlDto urlDto;

    @BeforeEach
    void setUp() {
        urlDto = UrlDto
                .builder()
                .url("url")
                .build();
    }

    @Test
    void testIsSavedToDB() {
        Hash hash = prepareHash();
        verify(urlRepository, times(1))
                .save(new Url(hash.getHash(), urlDto.getUrl()));
    }

    @Test
    void testIsSavedToRedis() {
        Hash hash = prepareHash();
        verify(urlCacheRepository, times(1))
                .save(hash.getHash(), urlDto.getUrl());
    }

    private Hash prepareHash() {
        Hash hash = new Hash("hash");
        when(hashCache.getHash()).thenReturn(hash);
        urlService.createShortUrl(urlDto);
        return hash;
    }
}
