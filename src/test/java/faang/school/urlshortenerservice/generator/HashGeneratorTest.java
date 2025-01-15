package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private HashRepository hashRepository;

    private long generateSize = 5000;
    private long getSize = 2700;
    private long minSize = 2500;
    private long hashForSchedulerSize = 7000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "generateSize", generateSize);
        ReflectionTestUtils.setField(hashGenerator, "getSize", getSize);
        ReflectionTestUtils.setField(hashGenerator, "minSize", minSize);
        ReflectionTestUtils.setField(hashGenerator, "hashForSchedulerSize", hashForSchedulerSize);
    }

    @Test
    void testDoesNotGenerateCacheWhenEnoughInDb() {
        when(hashRepository.count()).thenReturn(9000L);

        hashGenerator.generateHash();

        verify(hashRepository, times(0)).getUniqueNumbers(generateSize);
        verify(base62Encoder, times(0)).encode(any());
        verify(hashRepository, times(0)).saveAll(any());
    }

    @Test
    void testGenerateCacheTwoTimesWhenNotEnoughInDb() {
        when(hashRepository.count()).thenReturn(getSize * 0, getSize, generateSize * 2);

        hashGenerator.generateHash();

        verify(hashRepository, times(2)).getUniqueNumbers(generateSize);
        verify(base62Encoder, times(2)).encode(any());
        verify(hashRepository, times(2)).saveAll(any());
    }

    @Test
    void testFindAndDeleteReturnsHashes() {
        List<Hash> expectedHashes = Arrays.asList(new Hash("2"), new Hash("3"));
        when(hashRepository.findAndDelete(getSize)).thenReturn(expectedHashes);
        when(hashRepository.count()).thenReturn(minSize + 10);

        List<Hash> result = hashGenerator.findAndDelete();

        assertEquals(expectedHashes, result);
        verify(hashRepository, times(1)).findAndDelete(getSize);
        verify(hashRepository, times(1)).count();
        verify(hashRepository, never()).getUniqueNumbers(generateSize);
    }
}