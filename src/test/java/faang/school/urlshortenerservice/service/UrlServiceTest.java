package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.UrlBaza;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Test
    void testGenerateShortUrl() {
        String url = "http//www.new.ru";
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl(url);

        String firstElement = "qq";
        UrlBaza urlBaza = UrlBaza.builder()
                .hash(firstElement)
                .url(url)
                .build();
        when(hashCache.getHash()).thenReturn(firstElement);
        when(urlRepository.save(urlBaza)).thenReturn(urlBaza);
        urlService.generateShortUrl(urlDto);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(UrlBaza.class));
    }

    @Test
    void testReturnFullUrl() {
        String url = "http//www.new.ru";
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl(url);
        String firstElement = "qq";
        UrlBaza urlBaza = UrlBaza.builder()
                .hash(firstElement)
                .url(url)
                .build();
        when(urlRepository.findById(firstElement)).thenReturn(Optional.of(urlBaza));
        String result = urlService.returnFullUrl(firstElement);
        assertEquals(url, result);
    }
}