package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.hashservice.HashCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlMapper urlMapper;

    private UrlDto urlDto;
    private String hash;
    private Url url;
    private String link;

    @BeforeEach
    public void setUp() {
        link = "https://faang-school.atlassian.net/jira/software/c" +
                "/projects/BJS2/boards/32?selectedIssue=BJS2-17250";
        hash = "ewfn12";

        urlDto = UrlDto.builder()
                .url(link)
                .build();
        url = Url.builder()
                .url(link)
                .build();
    }

    @Test
    public void testCreateShortLinkIfExistInBd() {
        when(urlRepository.getHash(urlDto.getUrl())).thenReturn(hash);

        urlService.createShortLink(urlDto);

        verify(hashCache, times(0)).getHash();
        verify(urlMapper, times(0)).toEntity(urlDto);
        verify(urlCacheRepository, times(0)).save(url);
        verify(urlRepository, times(0)).save(url);
    }

    @Test
    public void testCreateShortLinkIfNotExistInBd() {
        when(urlRepository.getHash(urlDto.getUrl())).thenReturn(null);
        when(hashCache.getHash()).thenReturn(hash);
        when(urlMapper.toEntity(urlDto)).thenReturn(url);
        when(urlRepository.save(url)).thenReturn(url);

        urlService.createShortLink(urlDto);

        verify(urlRepository, times(1)).getHash(urlDto.getUrl());
        verify(hashCache, times(1)).getHash();
        verify(urlMapper, times(1)).toEntity(urlDto);
        verify(urlCacheRepository, times(1)).save(url);
        verify(urlRepository, times(1)).save(url);
    }
}
