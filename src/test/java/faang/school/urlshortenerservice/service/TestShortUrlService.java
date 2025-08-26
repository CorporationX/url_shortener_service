package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.property.ShortenerProperties;
import faang.school.urlshortenerservice.document.ShortUrl;
import faang.school.urlshortenerservice.dto.short_url.CreateShortUrlDto;
import faang.school.urlshortenerservice.exception.ConflictException;
import faang.school.urlshortenerservice.mapper.ShortUrlMapper;
import faang.school.urlshortenerservice.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestShortUrlService {
    private static final String CODE = "test";
    private static final String URL = "https://example.com/page";
    private static final int MAX_ATTEMPTS = 5;
    private static final String DOMAIN = "http://localhost:8080";
    private static final int LENGTH = 7;

    @InjectMocks
    private ShortUrlServiceImpl service;

    @Mock
    private ShortenerProperties properties;

    @Spy
    private ShortUrlMapper mapper = Mappers.getMapper(ShortUrlMapper.class);
    @Mock
    private ShortUrlRepository repository;
    @Mock
    private UrlShortenerService shortenerService;
    @Mock
    private ShortUrlCacheService cacheService;
    @Captor
    private ArgumentCaptor<String> codeCaptor;
    @Captor
    private ArgumentCaptor<ShortUrl> shortUrlArgumentCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "domain", DOMAIN);
        ReflectionTestUtils.setField(service, "length", LENGTH);
    }

    @Test
    public void testSuccessfulCreateShortUrl() {
        when(properties.getMaxAttempts()).thenReturn(MAX_ATTEMPTS);
        when(shortenerService.generateCode(LENGTH)).thenReturn(CODE);
        when(repository.existsByCode(CODE)).thenReturn(false);
        CreateShortUrlDto dto = new CreateShortUrlDto(URL);
        ShortUrl shortUrl = mapper.toShortUrl(dto);

        String result = service.create(dto);

        verify(repository, times(1)).existsByCode(CODE);
        verify(repository, times(1)).insert(shortUrlArgumentCaptor.capture());
        assertTrue(result.contains(result));
        verify(cacheService, times(1)).set(
                codeCaptor.capture(),
                eq(shortUrl.getOriginalUrl())
        );
    }

    @Test
    public void testfailCreateShortUrl() {
        when(properties.getMaxAttempts()).thenReturn(MAX_ATTEMPTS);
        when(shortenerService.generateCode(LENGTH)).thenReturn(CODE);
        when(repository.existsByCode(CODE)).thenReturn(true);
        CreateShortUrlDto dto = new CreateShortUrlDto(URL);

        assertThrows(ConflictException.class, () -> service.create(dto));
        verify(repository, times(MAX_ATTEMPTS)).existsByCode(CODE);
    }

    @Test
    public void testFindCachedUrl() {
        when(cacheService.get(CODE)).thenReturn(URL);

        String url = service.find(CODE);

        verify(cacheService, times(1)).get(CODE);
        verify(repository, never()).findByCodeOrThrow(any());
        verify(cacheService, never()).set(any(), any());
        assertEquals(URL, url);
        verifyNoMoreInteractions(cacheService);
    }

    @Test
    public void testFindNotCachedUrl() {
        when(cacheService.get(CODE)).thenReturn(null);
        ShortUrl shortUrl = makeShortUrl(CODE, URL);
        when(repository.findByCodeOrThrow(CODE)).thenReturn(shortUrl);

        String url = service.find(CODE);

        verify(cacheService, times(1)).get(CODE);
        verify(repository, times(1)).findByCodeOrThrow(CODE);
        verify(cacheService, times(1)).set(shortUrl.getCode(), shortUrl.getOriginalUrl());
        assertEquals(URL, url);
        verifyNoMoreInteractions(cacheService);
    }

    private ShortUrl makeShortUrl(String code, String url) {
        return ShortUrl.builder()
                .code(code)
                .originalUrl(url)
                .build();
    }
}
