package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.urlshortenerservice.test.utils.TestData.HASHES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCleanerServiceTest {
    private static final int DAYS = 365;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashService hashService;

    @InjectMocks
    private UrlCleanerService urlCleanerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlCleanerService, "days", DAYS);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testRemoveExpiredUrls_successful() {
        List<Hash> hashes = HASHES.stream()
                .map(Hash::new)
                .toList();
        when(urlRepository.getAndDeleteUrlsByDate(any(LocalDateTime.class))).thenReturn(HASHES);

        urlCleanerService.removeExpiredUrlsAndResaveHashes();

        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<List<String>> hashesCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Hash>> hashesEntityCaptor = ArgumentCaptor.forClass(List.class);

        verify(urlRepository).getAndDeleteUrlsByDate(dateCaptor.capture());
        verify(hashService).saveAllBatch(hashesEntityCaptor.capture());
        verify(urlCacheRepository).deleteAll(hashesCaptor.capture());

        assertThat(dateCaptor.getValue().toLocalDate()).isEqualTo(LocalDateTime.now().minusDays(DAYS).toLocalDate());
        assertThat(hashesCaptor.getValue()).isEqualTo(HASHES);
        assertThat(hashesEntityCaptor.getValue()).isEqualTo(hashes);
    }
}