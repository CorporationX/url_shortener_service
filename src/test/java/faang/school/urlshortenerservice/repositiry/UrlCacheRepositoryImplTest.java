package faang.school.urlshortenerservice.repositiry;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepositoryImpl;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlCacheRepositoryImplTest {

    @Mock
    UrlRepository urlRepository;
    @InjectMocks
    UrlCacheRepositoryImpl urlCacheRepositoryImpl;

    Url testUrl = Url.builder()
            .hash("abc")
            .build();

    @Test
    void testSaveUrlSuccessful() {

        urlCacheRepositoryImpl.save(anyString(), anyString());

        verify(urlRepository, times(1))
                .create(anyString(), anyString());
    }

    @Test
    void testGetUrlIfUrlNotFound() {
        when(urlRepository.findByHash(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlCacheRepositoryImpl.getUrl(anyString()));
    }

    @Test
    void testGetUrlSuccessful() {
        when(urlRepository.findByHash(anyString())).thenReturn(Optional.of(testUrl));

        urlCacheRepositoryImpl.getUrl(anyString());

        verify(urlRepository, times(1))
                .findByHash(anyString());
    }
}