package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entiity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorServiceTest {

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashGeneratorService hashGeneratorService;

    @Captor
    private ArgumentCaptor<List<Hash>> hashesCaptor;

    @Nested
    @DisplayName("generateHashesInternal tests")
    class GenerateHashesInternalTests {

        @Test
        @DisplayName("Should generate and save hashes successfully")
        void shouldGenerateAndSaveHashes() {
            List<Long> sequences = Arrays.asList(1L, 62L, 123L);
            List<Hash> expectedHashes = Arrays.asList(
                    new Hash("b"),    // 1 в base62
                    new Hash("ba"),   // 62 в base62
                    new Hash("b9")    // 123 в base62
            );
            when(hashRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));
            hashGeneratorService.generateHashesInternal(sequences);
            verify(hashRepository).saveAll(hashesCaptor.capture());
            List<Hash> actualHashes = hashesCaptor.getValue();
            assertThat(actualHashes)
                    .hasSize(3)
                    .extracting(Hash::getValue)
                    .containsExactly("b", "ba", "b9");
        }

        @Test
        @DisplayName("Should handle empty sequence list")
        void shouldHandleEmptySequences() {
            List<Long> emptySequences = Collections.emptyList();
            hashGeneratorService.generateHashesInternal(emptySequences);
            verify(hashRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Should handle large sequence numbers")
        void shouldHandleLargeSequences() {
            List<Long> largeSequences = Arrays.asList(1000000L, 9999999L);
            when(hashRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));
            hashGeneratorService.generateHashesInternal(largeSequences);
            verify(hashRepository).saveAll(hashesCaptor.capture());
            List<Hash> generatedHashes = hashesCaptor.getValue();
            assertThat(generatedHashes)
                    .hasSize(2)
                    .allMatch(hash -> hash.getValue().matches("[a-zA-Z0-9]+"));
        }
    }

    @Nested
    @DisplayName("base62Encode tests")
    class Base62EncodeTests {

        @Test
        @DisplayName("Should correctly encode different values")
        void shouldCorrectlyEncodeValues() throws Exception {
            Method encodeMethod = HashGeneratorService.class
                    .getDeclaredMethod("base62Encode", long.class);
            encodeMethod.setAccessible(true);
            assertThat(encodeMethod.invoke(hashGeneratorService, 0L)).isEqualTo("");
            assertThat(encodeMethod.invoke(hashGeneratorService, 1L)).isEqualTo("b");
            assertThat(encodeMethod.invoke(hashGeneratorService, 62L)).isEqualTo("ba");
            assertThat(encodeMethod.invoke(hashGeneratorService, 123L)).isEqualTo("b9");
        }

        @Test
        @DisplayName("Should handle edge cases")
        void shouldHandleEdgeCases() throws Exception {
            Method encodeMethod = HashGeneratorService.class
                    .getDeclaredMethod("base62Encode", long.class);
            encodeMethod.setAccessible(true);
            assertThat(encodeMethod.invoke(hashGeneratorService, Long.MAX_VALUE))
                    .asString()
                    .matches("[a-zA-Z0-9]+")
                    .hasSizeGreaterThan(10);
        }
    }

    @Test
    @DisplayName("Should save all generated hashes atomically")
    void shouldSaveHashesAtomically() {
        List<Long> sequences = Arrays.asList(1L, 2L, 3L);
        RuntimeException expectedError = new RuntimeException("Database error");
        when(hashRepository.saveAll(anyList())).thenThrow(expectedError);
        assertThatThrownBy(() -> hashGeneratorService.generateHashesInternal(sequences))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");
    }
}