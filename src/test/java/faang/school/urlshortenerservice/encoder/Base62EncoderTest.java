package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private static final long VALUE_1 = 1L;
    private static final long VALUE_2 = 12L;
    private static final long VALUE_3 = 100L;
    private static final long VALUE_4 = 1000L;
    private static final List<Long> INPUT_VALUES = List.of(VALUE_1, VALUE_2, VALUE_3, VALUE_4);

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void encode_ShouldReturnBase62EncodedStrings() {
        List<String> result = base62Encoder.encode(INPUT_VALUES);

        assertEquals(INPUT_VALUES.size(), result.size());
        for (int i = 0; i < INPUT_VALUES.size(); i++) {
            assertNotNull(result.get(i));
            assertFalse(result.get(i).isEmpty());
        }
    }

    @Test
    void encode_ShouldReturnEmptyListWhenInputIsEmpty() {
        List<String> result = base62Encoder.encode(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void encode_ShouldReturnCorrectEncodingForZero() {
        List<String> result = base62Encoder.encode(List.of(0L));
        assertEquals("", result.get(0));
    }
}
