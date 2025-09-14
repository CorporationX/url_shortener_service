package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.NewUrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapperImpl;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private LocalCacheService localCacheService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Spy
    private UrlMapperImpl urlMapper;

    @InjectMocks
    private UrlService urlService;

    private final String protocol = "http";
    private final String domain = "short.ly";


    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(urlService, "protocol", protocol);
        ReflectionTestUtils.setField(urlService, "domain", domain);
    }

    @Test
    void createShort_savesAndCachesUrlAndReturnsShortUrl() {
        // arrange
        String expectedHash = "abc123";
        String targetUrl = "https://example.com/page";

        when(localCacheService.getHash()).thenReturn(expectedHash);
        CreateUrlDto dto = new CreateUrlDto(targetUrl);

        NewUrlResponseDto result = urlService.createShort(dto);

        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        verify(urlRepository).save(urlCaptor.capture());
        Url savedUrl = urlCaptor.getValue();
        assertThat(savedUrl.getHash()).isEqualTo(expectedHash);
        assertThat(savedUrl.getUrl()).isEqualTo(targetUrl);

        verify(urlCacheRepository).put(expectedHash, targetUrl);
        assertThat(result.shortUrl()).isEqualTo(protocol+ "://" + domain + "/" + expectedHash);

        verify(localCacheService).getHash();
    }

    @Test
    void getOriginal_returnsFromCache_whenPresent() {
        String hash = "abc123";
        String cachedUrl = "https://cached.com";
        HashDto hashDto = new HashDto(hash);

        when(urlCacheRepository.get(hash)).thenReturn(cachedUrl);

        String result = urlService.getOriginal(hashDto);

        assertThat(result).isEqualTo(cachedUrl);
        verify(urlCacheRepository).get(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getOriginal_returnsFromDbAndCaches_whenNotInCache() {
        String hash = "def456";
        String dbUrl = "https://from-db.com";
        HashDto hashDto = new HashDto(hash);

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash))
                .thenReturn(Optional.of(Url.builder().hash(hash).url(dbUrl).build()));

        String result = urlService.getOriginal(hashDto);

        assertThat(result).isEqualTo(dbUrl);
        verify(urlRepository).findByHash(hash);
        verify(urlCacheRepository).put(hash, dbUrl);
    }

    @Test
    void getOriginal_throwsException_whenNotFoundAnywhere() {
        String hash = "missing";
        HashDto hashDto = new HashDto(hash);

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getOriginal(hashDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(hash);

        verify(urlRepository).findByHash(hash);
    }
}
