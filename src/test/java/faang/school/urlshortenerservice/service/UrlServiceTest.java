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

    @BeforeEach
    public void setUp() {
        urlDto = UrlDto.builder()
                .url("https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/32?selectedIssue=BJS2-17250")
                .build();
        hash = "ewfn12";
        url = Url.builder()
                .url("https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/32?selectedIssue=BJS2-17250")
                .build();
    }

    @Test
    public void testCreateShortLinkIfExistInCache() {
        when(urlCacheRepository.getHash(urlDto)).thenReturn(hash);

        urlService.createShortLink(urlDto);

        verify(urlRepository, times(0)).getHash(urlDto.getUrl());
    }

    @Test
    public void testCreateShortLinkIfNotExistInCache() {
        when(urlCacheRepository.getHash(urlDto)).thenReturn(null);
        when(urlRepository.getHash(urlDto.getUrl())).thenReturn(hash);

        urlService.createShortLink(urlDto);

        verify(urlRepository, times(1)).getHash(urlDto.getUrl());
    }

    @Test
    public void testCreateShortLinkIfExistInBd() {
        when(urlRepository.getHash(urlDto.getUrl())).thenReturn(hash);

        urlService.createShortLink(urlDto);

        verify(urlMapper, times(0)).toEntity(urlDto);
    }

    @Test
    public void testCreateShortLinkIfNotExistInBd() {
        when(urlRepository.getHash(urlDto.getUrl())).thenReturn(null);
        when(urlMapper.toEntity(urlDto)).thenReturn(url);
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.save(url)).thenReturn(url);

        urlService.createShortLink(urlDto);

        verify(urlMapper, times(1)).toEntity(urlDto);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(url);
    }
}
