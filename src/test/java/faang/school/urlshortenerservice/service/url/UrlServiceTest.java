package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private CacheProperties cacheProperties;

    private static final String HASH = "hash";
    private List<String> existingHashes;

    @BeforeEach
    public void init() {
        existingHashes = Arrays.asList(HASH);
        ReflectionTestUtils.setField(cacheProperties, "nonWorkingUrlTime", 1);
    }

    @Test
    @DisplayName("Success when clean DB")
    public void whenCleanDBThenDeleteOldUrlAndGetHashes() {
        when(urlRepository.getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime())).thenReturn(existingHashes);
        doNothing().when(hashRepository).saveAllHashesBatched(anyList());

        urlService.releaseExpiredHashes();

        verify(urlRepository).getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime());
        verify(hashRepository).saveAllHashesBatched(anyList());
    }
}