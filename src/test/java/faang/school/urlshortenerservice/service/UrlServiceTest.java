package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.LocalCache;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.urlshortenerservice.util.TestDataFactory.HASH;
import static faang.school.urlshortenerservice.util.TestDataFactory.SHORT_URL;
import static faang.school.urlshortenerservice.util.TestDataFactory.URL;
import static faang.school.urlshortenerservice.util.TestDataFactory.createUrl;
import static faang.school.urlshortenerservice.util.TestDataFactory.createUrlDto;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @InjectMocks
    private UrlService urlService;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private LocalCache localCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlMapper urlMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        urlService = new UrlService(urlRepository, localCache, urlCacheRepository, urlMapper);
        urlService.setShortUrlPrefix("https://dd.n/");
    }

    @Test
    void givenUrlWhenSaveAndGetShortUrlThenReturnHash() {
        // given - precondition
        var url = createUrl();
        var urlDto = createUrlDto();

        when(localCache.getHash()).thenReturn(HASH);
        when(urlMapper.toEntity(urlDto)).thenReturn(url);
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        // when - action
        var actualResult = urlService.saveAndGetShortUrl(urlDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(SHORT_URL);
    }

    @Test
    void givenShortUrlWhenGetUrlThenReturnUrlFromCache() {
        // given - precondition
        when(urlCacheRepository.getUrl(HASH)).thenReturn(URL);

        // when - action
        var actualResult = urlService.getUrl(SHORT_URL);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(URL);

        verify(urlCacheRepository, times(1)).getUrl(HASH);
        verifyNoInteractions(urlRepository);
    }
    @Test
    void givenShortUrlWhenGetUrlThenReturnUrlFromDataBase() {
        // given - precondition
        var url = createUrl();

        when(urlCacheRepository.getUrl(HASH)).thenReturn(null);
        when(urlRepository.findById(HASH)).thenReturn(of(url));

        // when - action
        var actualResult = urlService.getUrl(SHORT_URL);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(URL);

        verify(urlCacheRepository, times(1)).getUrl(HASH);
        verify(urlRepository, times(1)).findById(HASH);
    }

    @Test
    void givenInvalidShortUrlWhenGetUrlThenThrowException() {
        // given - precondition
        when(urlCacheRepository.getUrl(HASH)).thenReturn(null);
        when(urlRepository.findById(HASH)).thenReturn(empty());

        // when - action
        // then - verify the output
        assertThatThrownBy(() -> urlService.getUrl(SHORT_URL))
                .hasMessageContaining("No URL find")
                        .isInstanceOf(IllegalArgumentException.class);

        verify(urlCacheRepository, times(1)).getUrl(HASH);
        verify(urlRepository, times(1)).findById(HASH);
    }
}