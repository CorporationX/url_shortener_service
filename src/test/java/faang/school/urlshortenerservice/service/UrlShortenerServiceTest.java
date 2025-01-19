package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Test
    void testShortUrl() {
        String hash = "a4f";
        Mockito.when(hashCache.getHash()).thenReturn("a4f");
        Mockito.when(urlRepository.save(any())).thenAnswer(a -> a.getArgument(0));
        Mockito.doNothing().when(urlCacheRepository).save(any(), any());

        UrlDto urlDto = UrlDto.builder()
                .url("http://localhost:80/test")
                .build();
       HashDto hashDto = urlShortenerService.shortUrl(urlDto);
       String actualHash = hashDto.getHash();

       assertEquals(hash, actualHash);
    }
}
