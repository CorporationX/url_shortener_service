package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.properties.CleanSchedulerProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanSchedulerTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private CleanSchedulerProperties properties;

    @InjectMocks
    private CleanScheduler cleanScheduler;

    private final List<Hash> testHashes = List.of(
            Hash.builder().hash("hash1").build(),
            Hash.builder().hash("hash2").build()
    );

    @BeforeEach
    void setUp() {
        when(properties.getExpirationDays()).thenReturn(30);
    }

    @Test
    void testDeleteUnusedUrlsSuccessfully() {
        when(urlRepository.deleteOldUrlsAndReturnHashes(30)).thenReturn(testHashes);

        cleanScheduler.deleteUnusedUrls();

        verify(urlRepository).deleteOldUrlsAndReturnHashes(30);
        verify(hashRepository).saveAll(testHashes);
    }

    @Test
    void testDeleteUnusedUrlsWhenNoResults() {
        when(urlRepository.deleteOldUrlsAndReturnHashes(30)).thenReturn(List.of());

        cleanScheduler.deleteUnusedUrls();

        verify(urlRepository).deleteOldUrlsAndReturnHashes(30);
        verify(hashRepository, never()).saveAll(any());
    }
}
