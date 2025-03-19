package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.LocalCache;
import faang.school.urlshortenerservice.service.cache.UrlCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private LocalCache localCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCache urlCache;

    @InjectMocks
    private UrlService urlService;

    private static final String ORIGINAL_URL = "https://example.com";
    private static final String HASH = "abc123";

    @BeforeEach
    void setUp() {
        // Общая настройка для тестов
    }

    @Test
    void createShortUrl_ShouldCreateAndReturnHash() {
        // given
        when(localCache.getHash()).thenReturn(HASH);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        // when
        String result = urlService.createShortUrl(ORIGINAL_URL);

        // then
        assertThat(result).isEqualTo(HASH);
        verify(localCache).getHash();
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void getOriginalUrl_ShouldReturnOriginalUrl() {
        // given
        when(urlCache.getOriginalUrl(HASH)).thenReturn(ORIGINAL_URL);

        // when
        String result = urlService.getOriginalUrl(HASH);

        // then
        assertThat(result).isEqualTo(ORIGINAL_URL);
        verify(urlCache).getOriginalUrl(HASH);
    }

    @Test
    void deleteUrls_ShouldDeleteAllUrls() {
        // given
        List<Url> urls = List.of(
            Url.builder().id(1L).hash(HASH).originalUrl(ORIGINAL_URL).build()
        );

        // when
        urlService.deleteUrls(urls);

        // then
        verify(urlRepository).deleteAll(urls);
    }

    @Test
    void getPageExpiredUrls_ShouldReturnExpiredUrls() {
        // given
        LocalDateTime expiredDateTime = LocalDateTime.now().minusHours(24);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Url> expiredUrls = List.of(
            Url.builder()
                .id(1L)
                .hash(HASH)
                .originalUrl(ORIGINAL_URL)
                .createdAt(expiredDateTime.minusHours(1))
                .build()
        );
        Page<Url> expectedPage = new PageImpl<>(expiredUrls);

        when(urlRepository.findByCreatedAtBefore(expiredDateTime, pageRequest))
            .thenReturn(expectedPage);

        // when
        Page<Url> result = urlService.getPageExpiredUrls(expiredDateTime, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getHash()).isEqualTo(HASH);
        verify(urlRepository).findByCreatedAtBefore(expiredDateTime, pageRequest);
    }
} 