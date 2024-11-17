package faang.school.urlshortenerservice.service.cleaner;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.service.url.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
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
    private HashService hashService;

    @Mock
    private CacheProperties cacheProperties;

    @Test
    @DisplayName("When method called then should get list of expired urls and save their hashes")
    void whenMethodCalledThenGetListEntitiesAndSaveTheirHashes() {
        Url url = Url.builder()
                .hash(HASH)
                .build();

        when(cacheProperties.getYearsToUrlExpiration())
                .thenReturn(EXPIRATION_URL);
        when(urlService.findAndReturnExpiredUrls(anyInt()))
                .thenReturn(List.of(url));

        cleanerService.clearExpiredUrls();

        verify(urlService)
                .findAndReturnExpiredUrls(anyInt());
        verify(hashService)
                .saveHashes(anyList());
    }
}