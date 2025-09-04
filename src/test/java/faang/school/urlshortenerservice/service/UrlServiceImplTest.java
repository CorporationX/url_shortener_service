package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCache;
import faang.school.urlshortenerservice.dto.short_url.CreateShortUrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {
    private static final String DOMAIN = "test";
    private static final String CODE = "code";
    private static final String URL = "url";

    @InjectMocks
    private UrlServiceImpl service;

    @Mock
    private HashCache cache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCache urlCache;
    @Spy
    private UrlMapper urlMapper = Mappers.getMapper(UrlMapper.class);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "domain", DOMAIN);
    }

    @Test
    public void testCreateShortUrl() {
        when(cache.getHash()).thenReturn(CODE);
        CreateShortUrlDto dto = CreateShortUrlDto.builder().url(URL).build();

        service.createShortUrl(dto);

        verify(cache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any());
        verify(urlCache, times(1)).set(CODE, dto.url());
    }

    @Test
    public void testGetOriginalUrl() {
        service.getOriginalUrl(CODE);

        verify(urlCache, times(1)).get(CODE);
    }
}
