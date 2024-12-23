package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @InjectMocks
    private UrlServiceImpl urlService;
    private Url url;
    private UrlDto urlDto;

    @BeforeEach
    public void setUp() {
        url = Url.builder()
                .hash("ADS34D")
                .url("http://newtest.com")
                .created_at(LocalDateTime.now())
                .build();
        urlDto = UrlDto.builder()
                .url("http://newtest.com")
                .build();
    }

    @Test
    public void testGetUrlSuccess() {
        String hash = "ADS34D";
        String expectedUrl = "http://newtest.com";
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getUrl(hash);

        assertEquals(expectedUrl, result);
        verify(urlRepository).findByHash(hash);
    }

    @Test
    public void testGetUrlNotFound() {
        String hash = "AfLvb4";

        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getUrl(hash));

        assertEquals("No url was found for this hash: AfLvb4", exception.getMessage());
        verify(urlRepository).findByHash(hash);
    }

    @Test
    public void getShortUrlSuccess() {
        String hash = "ADS34D";
        when(urlRepository.findByUrl(urlDto.url())).thenReturn(Optional.of(hash));

        String shortUrl = urlService.getShortUrl(urlDto);

        verify(urlRepository, never()).save(any());
    }

    @Test
    public void testGetShortUrl_whenUrlDoesNotExist() {
        String newHash = "xyz789";

        when(urlRepository.findByUrl(urlDto.url()))
                .thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn(newHash);

        String shortUrl = urlService.getShortUrl(urlDto);

        verify(urlRepository).save(any(Url.class));
    }
}
