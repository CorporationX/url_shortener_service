package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.executor.ExecutorServiceConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorServiceConfig executorServiceConfig;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    private static final int COUNT = 10;
    private List<Long> numbers;
    private List<Hash> hashes;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(hashGenerator, "amount", COUNT);
        ReflectionTestUtils.setField(hashGenerator, "sublistLength", COUNT);
        numbers = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        hashes = Arrays.asList(new Hash("a"), new Hash("s"), new Hash("d"), new Hash("f"), new Hash("g"),
                new Hash("h"), new Hash("j"), new Hash("k"), new Hash("l"), new Hash("z"));
    }

    @Test
    @DisplayName("Success when generate batch")
    public void whenGenerateBatchThenSaveGeneratedHashes() {
        when(hashRepository.getUniqueNumbers(COUNT)).thenReturn(numbers);
        when(executorServiceConfig.executor()).thenReturn(Executors.newFixedThreadPool(10));
        when(base62Encoder.encode(numbers)).thenReturn(hashes);
        doNothing().when(hashRepository).saveAllHashesBatched(anyList());

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(COUNT);
        verify(hashRepository).saveAllHashesBatched(anyList());
    }
}