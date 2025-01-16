package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Captor
    private ArgumentCaptor<Url> urlCaptor;

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlValidator validator;

    private UrlDto urlDto;
    private Hash hash;
    private Url url;

    @BeforeEach
    public void setUp() {
        urlDto = UrlDto.builder().url("https://www.google.com/search?q=amsterdam+sights&rlz=1C5CHFA_enAM1020AM1022&sxsrf=AJOqlz" +
                "VpeoKgccah6fWoJknYVkBsUzU26A:1678654067076&source=lnms&tbm=isch&sa=X&" +
                "ved=2ahUKEwj6qPnaodf9AhWkgf0HHYwjBvwQ_AUoAXoECAEQAw&biw=1440&bih=789&dpr=2#imgrc=2F4KvjYofOuZIM").build();
        hash = new Hash();
        hash.setHash("romabest");
        url = Url.builder().url("search?q=amsterdam+sights&rlz=1C5CHFA_enAM1020AM1022&sxsrf=AJOqlz" +
                "VpeoKgccah6fWoJknYVkBsUzU26A:1678654067076&source=lnms&tbm=isch&sa=X&" +
                "ved=2ahUKEwj6qPnaodf9AhWkgf0HHYwjBvwQ_AUoAXoECAEQAw&biw=1440&bih=789&dpr=2#imgrc=2F4KvjYofOuZIM")
                .hash(hash.getHash()).build();
    }

    @Test
    void testGetUrlHashSuccess() {
        when(hashCache.getHash()).thenReturn(hash.getHash());
        String shortUrl = urlService.getUrlHash(urlDto);

        verify(urlRepository).save(urlCaptor.capture());
        verify(urlCacheRepository).save(urlCaptor.capture());

        assertEquals(shortUrl, "https://www.google.com/romabest");
    }

    @Test
    void testGetOriginalUrl() {
        when(urlCacheRepository.findByHash(hash.getHash())).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash.getHash())).thenReturn(Optional.of(url));
        String ordinalUrl = urlService.getOriginalUrl("https://www.google.com/romabest");

        verify(urlCacheRepository).save(urlCaptor.capture());
        assertEquals(ordinalUrl, "https://www.google.com/search?q=amsterdam+sights&rlz=1C5CHFA_enAM1020AM1022&sxsrf=AJOqlz" +
                "VpeoKgccah6fWoJknYVkBsUzU26A:1678654067076&source=lnms&tbm=isch&sa=X&" +
                "ved=2ahUKEwj6qPnaodf9AhWkgf0HHYwjBvwQ_AUoAXoECAEQAw&biw=1440&bih=789&dpr=2#imgrc=2F4KvjYofOuZIM");
    }

    @Test
    void testGetOriginalUrlWithException() {
        when(urlCacheRepository.findByHash(hash.getHash())).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash.getHash()))
                .thenThrow(new DataValidationException("Cannot find longUrl from hash: " + hash.getHash()));

        assertThrows(DataValidationException.class, () -> urlService.getOriginalUrl("https://www.google.com/romabest"));
    }

}
