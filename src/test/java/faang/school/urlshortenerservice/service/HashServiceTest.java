package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @Spy
    @InjectMocks
    private HashService hashService;

    @Test
    public void generateBatch_ShouldCallRepositoryAndSaveHashes() throws Exception {
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);

        List<String> encodedHashes = Arrays.asList("a", "b", "c");
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        CompletableFuture<Void> future = hashService.generateBatch();
        future.get();
        verify(hashRepository, times(1)).saveAll(argThat(iterable -> {
            List<Hash> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list.size() == 3 &&
                    list.get(0).getHash().equals("a") &&
                    list.get(1).getHash().equals("b") &&
                    list.get(2).getHash().equals("c");
        }));
    }

    @Test
    public void getHashes_WhenEnoughHashesAvailable_ShouldReturnAll() {
        List<Hash> batch = Arrays.asList(
                new Hash(null, "hash1"),
                new Hash(null, "hash2"),
                new Hash(null, "hash3")
        );
        when(hashRepository.getHashBatch(3)).thenReturn(batch);

        List<String> result = hashService.getHashes(3);
        assertThat(result).containsExactly("hash1", "hash2", "hash3");
        verify(hashRepository, times(1)).getHashBatch(3);
        verify(hashRepository, never()).saveAll(any());
    }

    @Test
    public void getHashes_WhenNotEnoughHashesAvailable_ShouldGenerateAdditionalAndReturnCombined() throws Exception {
        List<Hash> initialBatch = Arrays.asList(
                new Hash(null, "hash1"),
                new Hash(null, "hash2")
        );
        List<Hash> additionalBatch = Arrays.asList(
                new Hash(null, "hash3"),
                new Hash(null, "hash4")
        );
        when(hashRepository.getHashBatch(eq(4L))).thenReturn(initialBatch);
        when(hashRepository.getHashBatch(eq(2L))).thenReturn(additionalBatch);

        doReturn(CompletableFuture.completedFuture(null)).when(hashService).generateBatch();

        List<String> result = hashService.getHashes(4);
        assertThat(result).containsExactly("hash1", "hash2", "hash3", "hash4");
        verify(hashService, times(1)).generateBatch();
    }
}