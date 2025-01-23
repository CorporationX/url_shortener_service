package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.service.hash.encoder.Encoder;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    private static final int BATCH_SIZE = 100;
    private static final int HASH_LENGTH = 6;
    private static final int ENCODING_BATCH_SIZE = 12;
    private static final int THREADS_NUMBER = 4;

    @Mock
    private HashRepository hashRepository;
    @Mock
    private Encoder encoder;
    @InjectMocks
    private HashGenerator hashGenerator;
    @Captor
    private ArgumentCaptor<List<String>> captor;

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", BATCH_SIZE);
        ReflectionTestUtils.setField(hashGenerator, "encodingBatchSize", ENCODING_BATCH_SIZE);
        ReflectionTestUtils.setField(hashGenerator, "hashLength", HASH_LENGTH);
        ReflectionTestUtils.setField(hashGenerator, "pool", Executors.newFixedThreadPool(THREADS_NUMBER));
    }

    @RepeatedTest(10)
    void testGenerateBatch() {
        List<Long> numbersFromSequence = LongStream.rangeClosed(1, BATCH_SIZE).boxed().toList();
        when(hashRepository.getUniqueNumbers(BATCH_SIZE)).thenReturn(numbersFromSequence);
        when(encoder.encode(anyList(), eq(HASH_LENGTH)))
                .thenAnswer(args -> ((List<Long>) args.getArguments()[0]).stream().map(number -> "hash" + number).toList());
        List<String> expected = new ArrayList<>(numbersFromSequence.stream().map(number -> "hash" + number).toList());

        hashGenerator.generateBatch();

        verify(hashRepository).save(captor.capture());
        List<String> actual = captor.getValue();
        expected = expected.stream().sorted().toList();
        actual = actual.stream().sorted().toList();
        assertArrayEquals(expected.toArray(new String[0]), actual.toArray(new String[0]));
    }
}