package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Encoder<Long> encoder;
    private final int hashBatchSize = 50;

    private HashGenerator hashGenerator;


    @BeforeEach
    void setUp() {
        hashGenerator = new HashGenerator(hashRepository, encoder, hashBatchSize);
    }

    @Test
    public void whenGenerateHashBatchSuccessfully() {
        hashGenerator.generateHashBatch();
        verify(hashRepository).getUniqueNumbers(hashBatchSize);;
        verify(encoder).encode(any());
        verify(hashRepository).saveAll(any());
    }

    @Test
    public void whenGetHashesThen() {
        List<Hash> hashes = List.of(new Hash("aB78"));
        when(hashRepository.getHashBatch(1)).thenReturn(hashes);
        assertThat(hashGenerator.getHashes(1)).isEqualTo(hashes);
    }
}