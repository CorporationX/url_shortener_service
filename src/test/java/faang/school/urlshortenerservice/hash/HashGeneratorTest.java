package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    private List<Long> anyLongs;
    private List<Hash> anyHashes;

    @BeforeEach
    public void setUp() throws Exception {
        int anyCacheCapacity = 5;
        anyLongs = List.of(1L, 2L, 3L, 4L, 5L);
        anyHashes = List.of(new Hash("a"), new Hash("b"), new Hash("c"), new Hash("d"), new Hash("e"));
        Field cacheCapacity = HashGenerator.class.getDeclaredField("cacheCapacity");
        cacheCapacity.setAccessible(true);
        cacheCapacity.set(hashGenerator, anyCacheCapacity);
    }

    @Test
    public void generateHashes_SuccessfullySavesIntoCache() {
        when(hashRepository.getUniqueNumbers(any(Integer.class))).thenReturn(anyLongs);
        when(base62Encoder.encode(any(List.class))).thenReturn(List.of("anyString"));
        when(hashRepository.saveAll(any(List.class))).thenReturn(anyHashes);

        hashGenerator.generateHashes();

        verify(hashRepository, times(1)).getUniqueNumbers(any(Integer.class));
        verify(base62Encoder, times(1)).encode(any(List.class));
        verify(hashRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    public void getHashes_NotEnoughHashesInRepository() {
        Hash anyHash = new Hash("anyHash");
        when(hashRepository.getHashBatchAndDelete(any(Integer.class)))
                .thenReturn(new ArrayList<>())
                .thenReturn(List.of(anyHash));
        assertEquals(List.of("anyHash"), hashGenerator.getHashes(1));
        verify(hashRepository, times(2)).getHashBatchAndDelete(any(Integer.class));
        verify(hashRepository, times(1)).getUniqueNumbers(any(Integer.class));
        verify(base62Encoder, times(1)).encode(any(List.class));
        verify(hashRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    public void getHashes_EnoughHashesInRepository() {
        when(hashRepository.getHashBatchAndDelete(any(Integer.class))).thenReturn(anyHashes);
        assertEquals(List.of("a", "b", "c", "d", "e"), hashGenerator.getHashes(5));
        verify(hashRepository, times(1)).getHashBatchAndDelete(any(Integer.class));
        verify(hashRepository, never()).getUniqueNumbers(any(Integer.class));
        verify(base62Encoder, never()).encode(any(List.class));
        verify(hashRepository, never()).saveAll(any(List.class));
    }
}
