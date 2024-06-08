package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlMapper urlMapper;
    @InjectMocks
    private UrlService urlService;

    @Test
    public void createUrlTest() {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("https://www.youtube.com/");

        String hash = "adewf";
        Url url = new Url();
        url.setHash(hash);
        url.setUrl("https://www.youtube.com/");
        Mockito.when(hashCache.getHash()).thenReturn(hash);
        Mockito.when(urlRepository.save(url)).thenReturn(url);
        Mockito.when(urlMapper.toDto(url)).thenReturn(urlDto);

        UrlDto result = urlService.createShortUrl(urlDto);

        Mockito.verify(hashCache, Mockito.times(1)).getHash();
        Mockito.verify(urlCacheRepository, Mockito.times(1)).putUrl(hash, urlDto.getUrl());
        Mockito.verify(urlRepository, Mockito.times(1)).save(url);
        Mockito.verify(urlMapper, Mockito.times(1)).toDto(url);

        Assertions.assertEquals(urlDto, result);
    }

    @Test
    public void getRedirectViewTest() {
        String hash = "adewf";
        String url = "https://www.youtube.com/";

        RedirectView redirectView = new RedirectView(url);


        Mockito.when(urlCacheRepository.getUrl(hash)).thenReturn(Optional.of(url));

        RedirectView result = urlService.getRedirectView(hash);

        Mockito.verify(urlCacheRepository, Mockito.times(1)).getUrl(hash);

        Assertions.assertEquals(redirectView.getUrl(), result.getUrl());
    }
}
