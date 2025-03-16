package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void testEncodeValidNumber() {
        assertEquals("a", encoder.encode(10));
        assertEquals("Y", encoder.encode(60));
        assertEquals("jU", encoder.encode(1234));
        assertEquals("1g8", encoder.encode(4844));
    }

    @Test
    public void testEncodeInvalidNumber() {
        assertThrows(DataValidationException.class, () -> encoder.encode(0));
        assertThrows(DataValidationException.class, () -> encoder.encode(-1));
    }
}