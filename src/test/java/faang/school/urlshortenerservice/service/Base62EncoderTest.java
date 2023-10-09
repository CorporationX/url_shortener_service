package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void testEncode() {
        List<Long> numbers = List.of(1L, 2L, 3L, 125L, 1354123L, 937821L, 31321321312321L, 125L);
        List<String> expected = List.of("1", "2", "3", "21", "5gGh", "3vy9", "8tQciGpd", "21");
        assertEquals(expected, base62Encoder.encode(numbers));
    }
}