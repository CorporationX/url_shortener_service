package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    public void testEncodeSuccessfully() {
        List<Long> numbers = List.of( 1L, 62L, 12345L, 3844L);

        List<String> result = base62Encoder.encode(numbers);

        List<String> expected = List.of("1", "10", "3D7", "100");
        assertEquals(expected, result);
    }
}