package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    HashCache hashCache;
    @Mock
    HashRepository hashRepository;
    @InjectMocks
    UrlService urlService;

    String hash = "hash-";
    String url = "url";
    UrlDto urlDto = new UrlDto(url);

    @Test
    void createShortLinkTest() {
        Mockito.when(hashCache.getHash()).thenReturn(hash);
        Mockito.when(hashRepository.saveUrlAndHash(Mockito.anyString(), Mockito.anyString())).thenReturn(hash);
        var result = urlService.createShortLink(urlDto);
        Assertions.assertEquals(result, hash);
    }

    @Test
    void getOriginUrlTest() {
        Mockito.when(hashRepository.getOriginalUrl(Mockito.anyString())).thenReturn(Optional.of(hash));
        var result = urlService.getOriginUrl(url);
        Assertions.assertEquals(result, hash);
    }

    @Test
    void getOriginUrlTestNotFound() {
        Mockito.when(hashRepository.getOriginalUrl(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getOriginUrl(url)
        );
    }
}
