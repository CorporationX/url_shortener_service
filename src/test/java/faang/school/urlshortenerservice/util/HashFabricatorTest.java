package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.ShortLinkHashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashFabricatorTest {

    @Mock
    private ShortLinkHashRepository hashRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private Base62Encoder encoder;
    @InjectMocks
    private HashFabricator hashFabricator;

    @Test
    public void testGetHashBatch() {
        List<Long> numbers = Arrays.asList(110l, 121l);
        Mockito.when(hashRepository.getListSequences(anyInt())).thenReturn(numbers);
        Mockito.when(encoder.encode(eq(numbers))).thenReturn(Arrays.asList("x", "y"));
        hashFabricator.getHashBatch();
        Mockito.verify(hashRepository).saveAll(anyList());
        verify(hashRepository, Mockito.times(1)).saveAll(anyList());
        verify(encoder).encode(numbers);
    }

    @Test
    public void testGetHashes() {
        List<String> correctHashes = Arrays.asList("karra", "mba");
        Mockito.when(hashRepository.getHashBatch(2)).thenReturn(correctHashes);
        assertEquals(correctHashes, hashFabricator.getHashes(2));
    }

    @Test
    public void testGetHashAsync() {
        List<String> correctHashes = Arrays.asList("abra", "cada", "bra");
        Mockito.when(hashRepository.getHashBatch(3)).thenReturn(correctHashes);
        CompletableFuture<List<String>> returningHashes = hashFabricator.getHashesAsync(3);
        returningHashes.thenAccept(hash -> assertEquals(correctHashes, hash));
    }
}