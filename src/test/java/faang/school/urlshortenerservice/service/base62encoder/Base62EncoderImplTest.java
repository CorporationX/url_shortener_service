package faang.school.urlshortenerservice.service.base62encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderImplTest {

    private static String BASE62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    @Spy
    private Base62EncoderImpl base62Encoder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(base62Encoder, "BASE62_CHARS", BASE62_CHARS);
    }

    @Test
    void testToEncode_WithCorrectArgument_ShouldSuccessEncode() {
        Long number = 1L;
        String hash = "B";

        String encodedValue = base62Encoder.encode(number);

        assertNotNull(encodedValue);
        assertEquals(encodedValue, hash);
    }

    @Test
    void testToEncode_WithInCorrectArgument_ThrowException() {
        Long number = -1L;

        assertThrows(IndexOutOfBoundsException.class, () ->
                base62Encoder.encode(number));
    }
}
