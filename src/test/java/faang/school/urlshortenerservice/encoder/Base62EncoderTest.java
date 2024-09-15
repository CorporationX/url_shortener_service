package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Base62EncoderTest {
    private Base62Encoder encoder = new Base62Encoder();

    @BeforeEach
    void init() {
        String base62Charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        encoder.setBase62Charsets(base62Charset);
    }

    @Test
    void encodeNumberTest() {
        List<Long> numbers = List.of(
                1l,
                19912903l
        );

        String expectedOne = "B";
        String expectedTwo = "1PiVB";
        List<String> actual = encoder.encode(numbers);
        assertEquals(expectedOne, actual.get(0));
        assertEquals(expectedTwo, actual.get(1));
    }

    @Test
    void encodeZeroTest() {
        List<Long> listWithZeroValue = List.of(0l);
        String expected = "A";
        List<String> actual = encoder.encode(listWithZeroValue);
        assertEquals(expected, actual.get(0));
    }

    @Test
    void encodeNegativeTest() {
        List<Long> listWithNegativeNumber = List.of(-1l);
        IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class,
                () -> encoder.encode(listWithNegativeNumber));
        String expectedMsg = "Number must be greater than or equal to 0";
        assertEquals(expectedMsg, actualException.getMessage());
    }
}
