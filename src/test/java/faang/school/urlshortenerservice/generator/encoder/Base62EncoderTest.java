package faang.school.urlshortenerservice.generator.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    public static final List<Long> TEST_NUMBERS = List.of(22L, 38L, 44L);
    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
    }

    @Test
    void testEncode() {
        List<String> expected = List.of("M", "c", "i");
        List<String> encoded = encoder.encode(TEST_NUMBERS);
        assertEquals(expected, encoded);
    }
}