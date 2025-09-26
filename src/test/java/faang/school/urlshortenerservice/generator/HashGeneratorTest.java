package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Spy
    @InjectMocks
    private HashGenerator hashGenerator;

    private Executor executor = Runnable::run;

    private final int maxRange = 3;
    private final int batchSize = 2;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 3);
        ReflectionTestUtils.setField(hashGenerator, "batchSize", 2);
        ReflectionTestUtils.setField(hashGenerator, "hashGeneratorExecutorService", executor);
    }

    @Test
    void generateHashes_savesEncodedHashesInBatches() {
        List<Long> range = List.of(1L, 2L, 3L);
        List<String> encoded = List.of("a", "b", "c");

        when(hashRepository.getNextRange(maxRange)).thenReturn(range);
        when(base62Encoder.base62EncodeList(range)).thenReturn(encoded);

        hashGenerator.generateHashes();

        verify(hashRepository).getNextRange(maxRange);
        verify(base62Encoder).base62EncodeList(range);

        verify(hashRepository).saveAllBatched(
                argThat(list -> list.size() == maxRange &&
                        list.get(0).getHash().equals("a") &&
                        list.get(1).getHash().equals("b") &&
                        list.get(2).getHash().equals("c")),
                eq(batchSize)
        );
    }

    @Test
    void generateHashes_handlesEmptyRange() {
        when(hashRepository.getNextRange(maxRange)).thenReturn(List.of());
        when(base62Encoder.base62EncodeList(List.of())).thenReturn(List.of());

        hashGenerator.generateHashes();

        verify(hashRepository).getNextRange(maxRange);
        verify(base62Encoder).base62EncodeList(List.of());
        verify(hashRepository).saveAllBatched(List.of(), batchSize);
    }

    @Test
    void getHashes_returnsEnoughOnFirstCall() throws Exception {
        int amountSameAsRequested = 5;
        when(hashRepository.findAndDelete(amountSameAsRequested))
                .thenReturn(List.of("h1", "h2", "h3", "h4", "h5"));

        List<String> result = hashGenerator.getHashes(amountSameAsRequested).get();

        assertThat(result).containsExactly("h1", "h2", "h3", "h4", "h5");
        verify(hashRepository).findAndDelete(amountSameAsRequested);
        verify(hashGenerator, never()).generateHashes();
    }

    @Test
    void getHashes_triggersGenerateHashesIfNotEnough() throws Exception {
        int amountMoreThanInDB = 5;
        when(hashRepository.findAndDelete(anyLong()))
                .thenReturn(List.of("h1", "h2"))
                .thenReturn(List.of("h3", "h4", "h5"));
        doNothing().when(hashGenerator).generateHashes();

        List<String> result = hashGenerator.getHashes(amountMoreThanInDB).get();

        assertThat(result).containsExactly("h1", "h2", "h3", "h4", "h5");
        verify(hashGenerator).generateHashes();
        verify(hashRepository, times(2)).findAndDelete(anyLong());
    }

    @Test
    void getHashes_handlesEmptyAtFirstThenFilled() throws Exception {
        int amountOnEmptyDB = 3;
        when(hashRepository.findAndDelete(anyLong()))
                .thenReturn(List.of())
                .thenReturn(List.of("h1", "h2", "h3"));
        doNothing().when(hashGenerator).generateHashes();

        List<String> result = hashGenerator.getHashes(amountOnEmptyDB).get();

        assertThat(result).containsExactly("h1", "h2", "h3");
        verify(hashGenerator).generateHashes();
        verify(hashRepository, times(2)).findAndDelete(anyLong());
    }

    @Test
    void getHashes_ThrowsIfNotAbleToGetHashes() throws Exception {
        int amountOnEmptyDB = 3;
        when(hashRepository.findAndDelete(anyLong()))
                .thenReturn(List.of("h1"))
                .thenReturn(List.of());
        doNothing().when(hashGenerator).generateHashes();

        CompletableFuture<List<String>> hashesFuture = hashGenerator.getHashes(amountOnEmptyDB);

        ExecutionException ee = assertThrows(ExecutionException.class, hashesFuture::get);
        Throwable cause = ee.getCause();
        assertInstanceOf(IllegalStateException.class, cause);

        verify(hashGenerator).generateHashes();
        verify(hashRepository, times(2)).findAndDelete(anyLong());
    }
}
