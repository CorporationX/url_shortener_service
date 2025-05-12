package faang.school.urlshortenerservice.model.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "sequenceAmount", 10);

        Logger logger = (Logger) LoggerFactory.getLogger(HashGenerator.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(HashGenerator.class);
        logger.detachAppender(logAppender);
    }

    @Test
    void testGenerateBatch_SuccessfulExecution_EncodesAndSavesHashes() {
        List<Long> sequenceNumbers = List.of(1L, 2L, 3L);
        List<String> encodedHashes = List.of("a", "b", "c");
        when(hashRepository.getUniqueNumbers(10)).thenReturn(sequenceNumbers);
        when(encoder.encode(sequenceNumbers)).thenReturn(encodedHashes);
        doNothing().when(hashRepository).save(anyList());

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(10);
        verify(encoder).encode(sequenceNumbers);
        verify(hashRepository).save(encodedHashes);
        List<ILoggingEvent> logs = logAppender.list;
        assertEquals(1, logs.size());
        assertEquals("Sequence numbers to be hashed: [1, 2, 3]", logs.get(0).getFormattedMessage());
    }

    @Test
    void testGenerateBatch_ExceptionThrown_LogsAndRethrows() {
        RuntimeException exception = new RuntimeException("Database error");
        when(hashRepository.getUniqueNumbers(10)).thenThrow(exception);

        assertThrows(RuntimeException.class, () -> hashGenerator.generateBatch());

        verify(hashRepository).getUniqueNumbers(10);
        verify(encoder, never()).encode(anyList());
        verify(hashRepository, never()).save(anyList());
        List<ILoggingEvent> logs = logAppender.list;
        assertEquals(1, logs.size());
        assertEquals("Exception during generating hashcodes: ", logs.get(0).getMessage());
    }
}