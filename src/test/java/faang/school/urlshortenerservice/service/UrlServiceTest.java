package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Spy
    private UrlMapper urlMapper = new UrlMapperImpl();
    @InjectMocks
    private UrlService urlService;

    private Url toSave;
    private Url fromSave;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final String url = "https://www.oracle.com/cis/";
    private final String formattedUrl = "www.oracle.com/cis";
    private final String hash = "6FgV8";

    @BeforeEach
    void setUp() {
        toSave = Url.builder()
                .hash(hash)
                .url(formattedUrl)
                .build();
        fromSave = Url.builder()
                .hash(hash)
                .url(formattedUrl)
                .createdAt(createdAt)
                .build();
    }

    @Test
    void associateHashWithURLTest() {
        UrlDto request = UrlDto.builder()
                .url(url)
                .build();

        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(toSave)).thenReturn(fromSave);

        UrlDto expected = UrlDto
                .builder()
                .hash(hash)
                .url(formattedUrl)
                .createdAt(createdAt)
                .build();

        UrlDto result = urlService.associateHashWithURL(request);

        assertEquals(expected, result);

        verify(hashCache).getHash();
        verify(urlRepository).save(toSave);
        verify(urlCacheRepository).save(fromSave);
    }

    @Test
    void saveTest() {
        when(urlRepository.save(toSave)).thenReturn(fromSave);

        Url result = urlRepository.save(toSave);

        assertEquals(fromSave, result);

        verify(urlRepository).save(toSave);
    }
}