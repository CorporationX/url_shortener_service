package faang.school.urlshortenerservice.hashGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @BeforeEach
    public void setup() {
        String base62Charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        encoder.setBase62Charset(base62Charset);
    }

    @Test
    public void test_Encode_LargeNumber() {

        long number = 1234567890L;
        String expected = "UfHiVB";
        String actual = encoder.encode(number);
        assertEquals(expected, actual);
    }

    @Test
    public void test_Encode_Zero() {
        long number = 0;
        String expected = ""; // This is the base62 representation of 0
        String actual = encoder.encode(number);
        assertEquals(expected, actual);
    }
}
