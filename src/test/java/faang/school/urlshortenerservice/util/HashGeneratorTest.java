package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Captor
    ArgumentCaptor<List<Hash>> captor;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    @DisplayName("Upload batch of hashes: success")
    void test_uploadBatch_success() {
        Long amountFromSequence = 3L;

        when(hashRepository.getUniqueNumbersFromSequence(amountFromSequence)).thenReturn(Arrays.asList(1L, 10L, 62L));
        when(base62Encoder.encode(1L)).thenReturn("1");
        when(base62Encoder.encode(10L)).thenReturn("A");
        when(base62Encoder.encode(62L)).thenReturn("10");

        hashGenerator.uploadBatch(amountFromSequence);

        verify(hashRepository, times(1)).getUniqueNumbersFromSequence(amountFromSequence);
        verify(base62Encoder, times(3)).encode(anyLong());
        verify(hashRepository, times(1)).saveAll(captor.capture());

        assertEquals(3, captor.getValue().size());
        assertEquals(Hash.class, captor.getValue().get(0).getClass());
    }
}