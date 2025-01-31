package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlShortenerProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    private UrlShortenerProperties urlShortenerProperties;

    @Captor
    ArgumentCaptor<List<Hash>> captor;

    @InjectMocks
    private HashService hashService;

    @BeforeEach
    void setUp() {
        urlShortenerProperties = UrlShortenerProperties.builder()
                .hashAmountToGenerate(3L)
                .hashDatabaseThresholdRatio(0.5)
                .hashAmountToLocalCache(3L)
                .build();
        hashService = new HashService(hashRepository, base62Encoder, urlShortenerProperties);
    }

    @Test
    @DisplayName("Upload batch of hashes: not enough in database - upload needed")
    void test_uploadHashInDatabaseIfNecessary_NotEnoughInDatabase_Uploads() throws Exception {

        when(hashRepository.count()).thenReturn(0L);
        when(hashRepository.getUniqueNumbersFromSequence(urlShortenerProperties.hashAmountToGenerate())).thenReturn(Arrays.asList(1L, 10L, 62L));
        when(base62Encoder.encode(1L)).thenReturn("1");
        when(base62Encoder.encode(10L)).thenReturn("A");
        when(base62Encoder.encode(62L)).thenReturn("01");

        CompletableFuture<Void> result = hashService.uploadHashInDatabaseIfNecessary();

        verify(hashRepository, times(1)).count();
        verify(hashRepository, times(1)).getUniqueNumbersFromSequence(urlShortenerProperties.hashAmountToGenerate());
        verify(base62Encoder, times(3)).encode(anyLong());
        verify(hashRepository, times(1)).saveAll(captor.capture());

        assertNotNull(result);
        assertNull(result.get());
        assertEquals(3, captor.getValue().size());
        assertEquals(Hash.class, captor.getValue().get(0).getClass());
    }

    @Test
    @DisplayName("Upload batch of hashes: not enough in database - upload in progress - nothing happens")
    void test_uploadHashInDatabaseIfNecessary_NotEnoughInDatabaseUploadInProgress_DoNothing() throws Exception {

        ReflectionTestUtils.setField(hashService, "uploadInProgressFlag", new AtomicBoolean(true));
        when(hashRepository.count()).thenReturn(0L);

        CompletableFuture<Void> result = hashService.uploadHashInDatabaseIfNecessary();

        verify(hashRepository, times(1)).count();
        verify(hashRepository, never()).getUniqueNumbersFromSequence(urlShortenerProperties.hashAmountToGenerate());
        verify(base62Encoder, never()).encode(anyLong());
        verify(hashRepository, never()).saveAll(captor.capture());

        assertNotNull(result);
        assertNull(result.get());
    }

    @Test
    @DisplayName("Upload batch of hashes: enough in database - upload not needed")
    void test_uploadHashInDatabaseIfNecessary_EnoughInDatabase_DoNotUploads() throws Exception {

        when(hashRepository.count()).thenReturn(10L);

        CompletableFuture<Void> result = hashService.uploadHashInDatabaseIfNecessary();

        verify(hashRepository, times(1)).count();
        verify(hashRepository, never()).getUniqueNumbersFromSequence(urlShortenerProperties.hashAmountToGenerate());
        verify(base62Encoder, never()).encode(anyLong());
        verify(hashRepository, never()).saveAll(captor.capture());

        assertNotNull(result);
        assertNull(result.get());
    }

    @Test
    @DisplayName("Test getting hashes from database")
    void test_getHashesFromDatabase_success() throws Exception {
        Long hashAmount = urlShortenerProperties.hashAmountToLocalCache();

        when(hashRepository.getHashes(hashAmount)).thenReturn(List.of(new Hash(), new Hash(), new Hash()));

        CompletableFuture<List<Hash>> result = hashService.getHashesFromDatabase();

        assertNotNull(result);
        assertEquals(3, result.get().size());
    }
}

