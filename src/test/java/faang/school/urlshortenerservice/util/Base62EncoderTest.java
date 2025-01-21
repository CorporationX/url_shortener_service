package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.unbrokendome.base62.Base62;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @Mock
    private HashRepository hashRepository;

    private Base62Encoder base62Encoder;

    @Test
    void testConstructor_FailedMin() {
        long[] minValue = Base62.decodeArray("00000100000");
        when(hashRepository.getNextUniqueNumber()).thenReturn(minValue[0] - 1);
        assertThrows(IllegalStateException.class, () -> base62Encoder = new Base62Encoder(hashRepository));
    }

    @Test
    void testConstructor_FailedMax() {
        long[] maxValue = Base62.decodeArray("00000zzzzzz");
        when(hashRepository.getNextUniqueNumber()).thenReturn(maxValue[0] + 1);
        assertThrows(IllegalStateException.class, () -> base62Encoder = new Base62Encoder(hashRepository));
    }

    @Test
    void testEncode() {
        when(hashRepository.getNextUniqueNumber()).thenReturn(1_000_000_000L);
        base62Encoder = new Base62Encoder(hashRepository);

        List<Long> numbers = List.of(0L, 1L, 61L, 62L);

        List<String> result = base62Encoder.encode(numbers);
        assertEquals("000000", result.get(0));
        assertEquals("000001", result.get(1));
        assertEquals("00000z", result.get(2));
        assertEquals("000010", result.get(3));
    }
}