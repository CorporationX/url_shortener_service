package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "62, 10",
            "3843, zz",
            "999999999999, "
    })
    void testEncode(long number, String expected) {
        List<String> result = base62Encoder.encode(List.of(number));

        if (expected == null || expected.isEmpty()) {
            assertEquals(List.of(), result);
        } else {
            assertEquals(List.of(expected), result);
        }
    }
}