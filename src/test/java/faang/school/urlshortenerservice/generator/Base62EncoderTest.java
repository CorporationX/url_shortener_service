package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    public void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    public void testEncodeWhenCalledWithNumbersThenReturnsBase62Strings() {
        List<Long> numbers = Arrays.asList(1L, 2L, 3L);
        List<String> expected = Arrays.asList("b", "c", "d");

        List<String> actual = base62Encoder.encode(numbers);

        assertEquals(expected, actual, "The encoded strings did not match the expected output");
    }

    @Test
    public void testEncodeWhenCalledWithEmptyListThenReturnsEmptyList() {
        List<Long> numbers = Collections.emptyList();
        List<String> expected = Collections.emptyList();

        List<String> actual = base62Encoder.encode(numbers);

        assertEquals(expected, actual, "The encoded strings did not match the expected output");
    }

    @Test
    public void testEncodeWhenCalledWithNullListThenThrowsNullPointerException() {
        List<Long> numbers = null;

        assertThrows(NullPointerException.class, () ->
                base62Encoder.encode(numbers), "Expected encode() to throw NullPointerException when called with null list");
    }
}