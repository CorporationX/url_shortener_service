package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    private static final String TEST_URL = "https://www.google.com/search?sca_esv=2e9d584eca4b7171&sxsrf=ADLYWIKyVWeQU8h7r5sglmzbhPLW571oow:1737252725068&q=%D0%BE%D1%87%D0%B5%D0%BD%D1%8C+%D1%81%D1%83%D0%BF%D0%B5%D1%80+%D0%BF%D1%83%D0%BF%D0%B5%D1%80+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BD%D1%8B%D0%B9+%D0%B7%D0%B0%D0%BF%D1%80%D0%BE%D1%81&spell=1&sa=X&ved=2ahUKEwi4g8Dc2oCLAxXa0AIHHYikHpgQBSgAegQIChAB&cshid=1737252759802978&biw=1920&bih=934&dpr=1";
    private static final String TEST_HASH = "baaaaa";

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    public void generateShortUlrWhenUrlExistsTest() {
        UrlDto urlDto = new UrlDto(TEST_URL);
        when(urlRepository.findHashByUrl(TEST_URL)).thenReturn(Optional.of(TEST_HASH));

        String result = urlService.generateShortUrl(urlDto);

        assertEquals(TEST_HASH, result);
    }

    @Test
    public void generateShortUlrWhenUrlNotExistTest() {
        UrlDto urlDto = new UrlDto(TEST_URL);
        when(urlRepository.findHashByUrl(TEST_URL)).thenReturn(java.util.Optional.empty());
        when(hashCache.getHash()).thenReturn(TEST_HASH);

        String result = urlService.generateShortUrl(urlDto);

        assertEquals(TEST_HASH, result);
    }

    @Test
    void getUrlWhenHashExistsTest() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.of(TEST_URL));

        String result = urlService.getUrl(TEST_HASH);

        assertEquals(TEST_URL, result);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getUrlWhenHashNotExistsTest() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(TEST_HASH)).thenReturn(Optional.of(TEST_URL));

        String result = urlService.getUrl(TEST_HASH);

        assertEquals(TEST_URL, result);
        verify(urlCacheRepository).save(TEST_HASH, TEST_URL);
    }

    @Test
    void getUrlWhenNotFoundTest() {
        when(urlCacheRepository.findByHash(TEST_HASH)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(TEST_HASH)).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> urlService.getUrl(TEST_HASH));

        assertEquals("URL not found for hash: %s".formatted(TEST_HASH), exception.getMessage());
    }
}
