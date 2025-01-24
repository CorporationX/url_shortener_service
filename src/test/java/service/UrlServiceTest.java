package service;

import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.UrlAssociation;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validate.UrlValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlValidate validator;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private UrlDto urlDto;
    private String hash;

    @BeforeEach
    void setUp() {
        hash = "v3f2";
        urlDto = new UrlDto("https://github.com/CorporationX/url_shortener_service/pull/828/files#diff-b035e36c2");
    }

    @Test
    void testGenerateShortUrl_NewUrl() {
        when(hashCache.getHash()).thenReturn(hash);
        when(validator.presenceOfUrl(urlDto.getOriginalUrl())).thenReturn(false);

        urlService.generateShortUrl(urlDto);

        verify(urlRepository).save(any(UrlAssociation.class));
        verify(urlCacheRepository).save(any(UrlAssociation.class));
    }

    @Test
    void testGenerateShortUrl_ExistingUrl() {
        when(validator.presenceOfUrl(urlDto.getOriginalUrl())).thenReturn(true);
        UrlAssociation existingUrl = new UrlAssociation();
        existingUrl.setHash(hash);
        when(urlRepository.findByUrl(urlDto.getOriginalUrl())).thenReturn(existingUrl);

        urlService.generateShortUrl(urlDto);

        verify(urlRepository, never()).save(any(UrlAssociation.class));
        verify(urlCacheRepository, never()).save(any(UrlAssociation.class));
    }

    @Test
    void testReturnFullUrl_ExistingHash() {
        String originalUrl = "https://example.com";
        when(urlCacheRepository.getOriginUrl(hash)).thenReturn(originalUrl);

        String result = urlService.returnFullUrl(hash);

        assertEquals(originalUrl, result);
    }

    @Test
    void testReturnFullUrl_NonExistingHash() {
        when(urlCacheRepository.getOriginUrl(hash)).thenReturn(null);
        UrlAssociation urlAssociation = new UrlAssociation();
        urlAssociation.setUrl("https://example.com");
        when(urlRepository.findById(hash)).thenReturn(Optional.of(urlAssociation));

        String result = urlService.returnFullUrl(hash);

        assertEquals(urlAssociation.getUrl(), result);
    }

    @Test
    void testReturnFullUrl_HashNotFound() {
        when(urlCacheRepository.getOriginUrl(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            urlService.returnFullUrl(hash);
        });
        assertEquals("For the specified hash the full URL is not in the database", exception.getMessage());
    }
}
