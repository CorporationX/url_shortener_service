package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Test cases of Base62EncoderTest")
public class Base62EncoderTest {

    private static final String NEGATIVE_VALUE_MESSAGE = "The encoder does not support negative numbers";

    @Test
    @DisplayName("encodeNumber - negative value")
    public void testEncodeNumberWithNegativeValue() {
        long negativeValue = -1L;

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> Base62Encoder.encodeNumber(negativeValue)
        );

        assertEquals(NEGATIVE_VALUE_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("encodeNumber - encoding of zero")
    public void testEncodeNumberWithZeroValue() {
        String expectedHash = "0";

        String actualHash = Base62Encoder.encodeNumber(0);

        assertEquals(expectedHash, actualHash);
    }

    @Test
    @DisplayName("encodeNumber - success")
    public void testEncodeNumberSuccess() {
        assertEquals("1", Base62Encoder.encodeNumber(1));
        assertEquals("A", Base62Encoder.encodeNumber(10));
        assertEquals("z", Base62Encoder.encodeNumber(61));
    }

    @Test
    @DisplayName("encodeNumbers - input list with negative value")
    public void testEncodeNumbersWithNegativeValue() {
        List<Long> input = List.of(-1L, 1L, 2L);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> Base62Encoder.encodeNumbers(input)
        );

        assertEquals(NEGATIVE_VALUE_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("encodeNumbers - input list with zero values")
    public void testEncodeNumbersWithZeroValues() {
        List<Long> input = List.of(0L, 0L, 1L);
        List<String> expected = List.of("0", "0", "1");

        assertEquals(expected, Base62Encoder.encodeNumbers(input));
    }


    @Test
    @DisplayName("encodeNumbers - success")
    public void testEncodeNumbersSuccess() {
        List<Long> input = List.of(1L, 10L, 61L);
        List<String> expected = List.of("1", "A", "z");

        assertEquals(expected, Base62Encoder.encodeNumbers(input));
    }
}
