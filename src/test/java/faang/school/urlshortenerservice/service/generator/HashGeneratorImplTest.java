package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorImplTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGeneratorImpl hashGenerator;

    private void batchSize(long value) {
        try {
            Field field = hashGenerator.getClass().getDeclaredField("batchSize");
            field.setAccessible(true);
            field.set(hashGenerator, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generateBatch_createsAndSavesEncodedHashes() {
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(Arrays.asList(1L, 2L, 3L));
        when(base62Encoder.encode(anyList())).thenReturn(Arrays.asList("a", "b", "c"));

        hashGenerator.generateBatch();

        InOrder inOrder = inOrder(hashRepository, base62Encoder);
        inOrder.verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        inOrder.verify(base62Encoder, times(1)).encode(Arrays.asList(1L, 2L, 3L));
        inOrder.verify(hashRepository, times(1)).saveAll(Arrays.asList("a", "b", "c"));
    }

    @Test
    void getBatch_generatesAndReturnsNewHashesWhenNotEnoughExist() {
        batchSize(2L);

        when(hashRepository.getHashBatch(anyLong())).thenReturn(new ArrayList<>(List.of("a"))).thenReturn(List.of("b"));
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(Collections.singletonList(1L));
        when(base62Encoder.encode(anyList())).thenReturn(List.of("b"));

        List<String> result = hashGenerator.getBatch();

        InOrder inOrder = inOrder(hashRepository, base62Encoder);
        inOrder.verify(hashRepository, times(1)).getHashBatch(anyLong());
        inOrder.verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        inOrder.verify(base62Encoder, times(1)).encode(List.of(1L));
        inOrder.verify(hashRepository, times(1)).saveAll(List.of("b"));

        assertIterableEquals(List.of("a", "b"), result);
    }

    @Test
    void getBatch_enoughHashes() {
        batchSize(1L);

        when(hashRepository.getHashBatch(anyLong())).thenReturn(Collections.singletonList("a"));

        List<String> result = hashGenerator.getBatch();

        InOrder inOrder = inOrder(hashRepository, base62Encoder);
        inOrder.verify(hashRepository, times(1)).getHashBatch(anyLong());
        inOrder.verifyNoMoreInteractions();

        assertIterableEquals(List.of("a"), result);
    }
}