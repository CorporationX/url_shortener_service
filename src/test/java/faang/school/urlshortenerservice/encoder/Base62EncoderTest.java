package faang.school.urlshortenerservice.encoder;


import faang.school.urlshortenerservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(encoder, "maxHashLength", 6);
    }

    @Test
    public void testEncodeValidNumber() {
        assertEquals("A", encoder.encode(10));
        assertEquals("y", encoder.encode(60));
        assertEquals("Ju", encoder.encode(1234));
        assertEquals("1G8", encoder.encode(4844));
    }

    @Test
    public void testEncodeInvalidNumber() {
        assertThrows(DataValidationException.class, () -> encoder.encode(0));
        assertThrows(DataValidationException.class, () -> encoder.encode(-1));
    }

    @Test
    public void testEncodeExceedSize() {
        assertThrows(IllegalStateException.class, () -> encoder.encode(2147483646));
    }
}
