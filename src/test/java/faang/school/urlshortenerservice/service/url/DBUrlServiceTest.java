package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.search.url.DBUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DBUrlServiceTest {

    @InjectMocks
    private DBUrlService dbUrlService;

    @Mock
    private UrlRepository urlRepository;

    private String url;
    private String hash;

    @BeforeEach
    public void setUp() {
        url = "http://example.com";
        hash = "abc123";
    }

    @Test
    public void testFindUrlWhenHashExists() {
        Url urlEntity = Url.builder().hash(hash).url(url).build();

        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlEntity));

        Optional<String> result = dbUrlService.findUrl(hash);

        assertTrue(result.isPresent());
        assertEquals(url, result.get());
    }

    @Test
    public void testFindUrlWhenHashDoesNotExist() {
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        Optional<String> result = dbUrlService.findUrl(hash);

        assertFalse(result.isPresent());
    }
}
