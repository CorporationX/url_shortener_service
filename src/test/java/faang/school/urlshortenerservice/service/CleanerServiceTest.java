package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.config.сache.CacheProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.cleanerService.CleanerService;
import faang.school.urlshortenerservice.service.urlService.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerServiceTest {

    private static final int EXPIRATION_URL = 1;

    private static final String HASH = "HASH";

    @InjectMocks
    private CleanerService cleanerService;

    @Mock
    private UrlService urlService;

    @Mock
    private CacheProperties cacheProperties;

    @Mock
    private HashRepository hashRepository;

    @Test
    void testClearExpiredUrls_successfulExecution() {
        when(cacheProperties.getUrlCleaningForYear()).thenReturn(EXPIRATION_URL);
        when(urlService.findAndReturnExpiredUrls(EXPIRATION_URL)).thenReturn(List.of(Url.builder().hash(HASH).build()));

        cleanerService.clearExpiredUrls();

        verify(cacheProperties).getUrlCleaningForYear();
        verify(urlService).findAndReturnExpiredUrls(EXPIRATION_URL);

        ArgumentCaptor<List<Hash>> captor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository).saveAllBatched(captor.capture());

        List<Hash> capturedHashes = captor.getValue();

        assertThat(capturedHashes).hasSize(1);
        assertEquals(capturedHashes.get(0).getHash(), HASH);
    }
}
