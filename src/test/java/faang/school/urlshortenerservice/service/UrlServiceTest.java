package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRedisRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private UrlRedisRepository urlRedisRepository;

    private String urlStr;
    private String hash;
    private Url url;
    private ShortUrlDto shortUrlDto;

    @BeforeEach
    public void setUp() {
        urlStr = "https://corp-x.com/";
        hash = "000001";
        url = Url.builder()
                .url(urlStr)
                .hash(hash)
                .createdAt(LocalDateTime.now())
                .build();
        shortUrlDto = new ShortUrlDto(hash);
    }

    @Test
    public void testGenerateShortUrl() {
        // arrange
        UrlDto urlDto = new UrlDto(urlStr);

        when(hashCache.getHash()).thenReturn(hash);
        when(urlMapper.toEntity(urlDto)).thenReturn(url);

        // act
        urlService.generateShortUrl(urlDto);

        // assert
        verify(urlMapper).toShortUrlDto(url, null);
    }

    @Test
    public void testGetUrlByShortUrlFromCache() {
        // arrange
        when(urlRedisRepository.getByHash(shortUrlDto.shortUrl())).thenReturn(url);

        // act
        urlService.getUrlByShortUrl(shortUrlDto);

        // assert
        verify(urlMapper).toUrlDto(url);
    }

    @Test
    public void testGetUrlByShortUrlFromDatabase() {
        // arrange
        when(urlRedisRepository.getByHash(shortUrlDto.shortUrl())).thenReturn(null);
        when(urlRepository.findById(shortUrlDto.shortUrl())).thenReturn(Optional.of(url));

        // act
        urlService.getUrlByShortUrl(shortUrlDto);

        // assert
        verify(urlMapper).toUrlDto(url);
    }

    @Test
    public void testGetByShortUrlNotFound() {
        // arrange
        when(urlRedisRepository.getByHash(shortUrlDto.shortUrl())).thenReturn(null);
        when(urlRepository.findById(shortUrlDto.shortUrl())).thenReturn(Optional.empty());

        // act and assert
        assertThrows(EntityNotFoundException.class, () -> urlService.getUrlByShortUrl(shortUrlDto));
    }

    @Test
    public void testDeleteOldShortUrls() {
        // arrange
        List<Url> deletedShortUrls = List.of(
                new Url("000001", urlStr, LocalDateTime.now()),
                new Url("000002", urlStr, LocalDateTime.now()),
                new Url("000003", urlStr, LocalDateTime.now()),
                new Url("000abc", urlStr, LocalDateTime.now())
        );
        List<String> hashes = List.of(
                "000001",
                "000002",
                "000003",
                "000abc"
        );
        when(urlRepository.deleteOldShortUrls()).thenReturn(deletedShortUrls);

        // act
        urlService.deleteOldShortUrls();

        // assert
        verify(hashRepository).save(hashes);
    }
}
