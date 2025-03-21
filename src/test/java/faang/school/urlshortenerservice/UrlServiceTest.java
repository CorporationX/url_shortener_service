package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.dto.UrlShortenerRequest;
import faang.school.urlshortenerservice.dto.UrlShortenerResponse;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.utils.HashCache;
import faang.school.urlshortenerservice.utils.UrlValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlValidator validator;
    @InjectMocks
    private UrlService urlService;

    @Test
    public void create_Success() {
        UrlShortenerRequest request = new UrlShortenerRequest("https://github.com/");
        Hash hash = Hash.builder().hash("XXX").build();

        when(hashCache.getHash()).thenReturn(hash);

        urlService.create(request);

        ArgumentCaptor<String> validateUrlCaptor = ArgumentCaptor.forClass(String.class);
        verify(validator, times(1)).isUrl(validateUrlCaptor.capture());
        Assertions.assertEquals(validateUrlCaptor.getValue(), request.endPoint());

        ArgumentCaptor<Url> savedUrlCaptor = ArgumentCaptor.forClass(Url.class);
        verify(urlRepository).save(savedUrlCaptor.capture());
        Assertions.assertEquals(savedUrlCaptor.getValue().getUrl(), request.endPoint());
        Assertions.assertEquals(savedUrlCaptor.getValue().getHash(), hash.getHash());

        ArgumentCaptor<String> cachedUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cachedHashCaptor = ArgumentCaptor.forClass(String.class);
        verify(urlCacheRepository).cacheCouple(cachedHashCaptor.capture(), cachedUrlCaptor.capture());
        Assertions.assertEquals(cachedUrlCaptor.getValue(), request.endPoint());
        Assertions.assertEquals(cachedHashCaptor.getValue(), hash.getHash());
    }

    @Test
    public void getEndPoint_UrlNotFound() {
        String hash = "XXX";
        Assertions.assertThrows(EntityNotFoundException.class, () -> urlService.getEndPoint(hash));
    }

    @Test
    public void getEndPoint_gettingFromCacheSuccess() {
        String expectedEndPoint = "https://github.com/";
        String hash = "XXX";

        when(urlCacheRepository.getEndPointByHash(hash)).thenReturn(expectedEndPoint);

        UrlShortenerResponse response = urlService.getEndPoint(hash);

        Assertions.assertEquals(response.endPoint(), expectedEndPoint);
        Assertions.assertTrue(response.shortLink().endsWith(hash));
    }

    @Test
    public void getEndPoint_gettingFromDbSuccess() {
        String expectedEndPoint = "https://github.com/";
        String hash = "XXX";

        Url url = Url.builder().url(expectedEndPoint).hash(hash).build();
        when(urlRepository.getUrlByHash(hash)).thenReturn(Optional.of(url));

        UrlShortenerResponse response = urlService.getEndPoint(hash);

        ArgumentCaptor<String> cachedUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> cachedHashCaptor = ArgumentCaptor.forClass(String.class);
        verify(urlCacheRepository).cacheCouple(cachedHashCaptor.capture(), cachedUrlCaptor.capture());
        Assertions.assertEquals(cachedUrlCaptor.getValue(), expectedEndPoint);
        Assertions.assertEquals(cachedHashCaptor.getValue(), hash);

        Assertions.assertEquals(response.endPoint(), expectedEndPoint);
        Assertions.assertTrue(response.shortLink().endsWith(hash));
    }
}
