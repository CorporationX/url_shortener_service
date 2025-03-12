package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private HashService hashService;

    private static final int MAX_RANGE = 10000;
    private static final String ENCODED_HASH = "abc123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashService, "maxRange", MAX_RANGE);
    }

    @Test
    void generateHashes_ShouldGenerateAndSaveHashes() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        when(hashRepository.getNextRange(MAX_RANGE)).thenReturn(numbers);
        when(base62Encoder.encode(anyLong())).thenReturn(ENCODED_HASH);

        hashService.generateHashes();

        verify(hashRepository).getNextRange(MAX_RANGE);
        verify(base62Encoder, times(numbers.size())).encode(anyLong());
    }

    @Test
    void getHashes_ShouldReturnRequestedAmount() {
        long amount = 5;
        List<Hash> initialHashes = new ArrayList<>(List.of(
                Hash.builder().hash(ENCODED_HASH).build(),
                Hash.builder().hash(ENCODED_HASH).build()
        ));
        long initSize = initialHashes.size();
        List<Hash> additionalHashes = new ArrayList<>(List.of(
                Hash.builder().hash(ENCODED_HASH).build()
        ));

        when(hashRepository.findAndDelete(amount))
                .thenReturn(initialHashes);
        when(hashRepository.findAndDelete(amount - initialHashes.size()))
                .thenReturn(additionalHashes);
        when(hashRepository.getNextRange(MAX_RANGE))
                .thenReturn(List.of(1L, 2L, 3L));

        List<Hash> result = hashService.getHashes(amount);

        assertThat(result).hasSize(3);
        verify(hashRepository).findAndDelete(amount);
        verify(hashRepository).getNextRange(MAX_RANGE);
        verify(hashRepository).findAndDelete(amount - initSize);
    }

    @Test
    void getHashesAsync_ShouldReturnCompletableFuture() {
        // given
        long amount = 5;
        List<Hash> hashes = new ArrayList<>(List.of(
                Hash.builder().hash(ENCODED_HASH).build(),
                Hash.builder().hash(ENCODED_HASH).build()
        ));

        when(hashRepository.findAndDelete(amount))
                .thenReturn(hashes);

        // when
        CompletableFuture<List<Hash>> future = hashService.getHashesAsync(amount);

        // then
        assertThat(future).isNotNull();
        List<Hash> result = future.join();
        assertThat(result).hasSize(2);
        verify(hashRepository).findAndDelete(amount);
    }

    @Test
    void saveHashes_ShouldSaveHashesToDatabase() {
        // given
        List<String> hashes = List.of("abc123", "def456", "ghi789");

        // when
        hashService.saveHashes(hashes);

        // then
        verify(jdbcTemplate).batchUpdate(
                eq("INSERT INTO hash (hash) VALUES (?)"),
                anyList()
        );
    }
} 